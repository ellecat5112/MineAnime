package com.animecore.client.vfx;

import com.animecore.entity.EnergyProjectileEntity;
import com.animecore.vfx.VfxStyle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

/**
 * Technique projectiles as animated, texture-free elemental balls: a glowing core plus spiral flame/water/wind arms
 * that wrap in from behind toward the ball as it flies (Great Fireball), lightning arcs, wind rings (Rasengan-like),
 * tumbling rock / wood chunks for earth-type balls and dark matter for shadow. Particles are added by
 * {@link VfxManager#tickProjectiles}.
 */
public final class EnergyProjectileRenderer extends EntityRenderer<EnergyProjectileEntity> {
    public EnergyProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public Identifier getTexture(EnergyProjectileEntity entity) {
        return Identifier.ofVanilla("textures/misc/white.png");
    }

    @Override
    public void render(EnergyProjectileEntity e, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider buffers, int light) {
        VfxStyle style = e.getStyle();
        int color = e.getColor();
        float s = e.getSize();
        double R = Math.max(0.2, s * 0.7);
        float time = e.age + tickDelta;
        Vec3d cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        Vec3d c = e.getLerpedPos(tickDelta).add(0, s / 2, 0).subtract(cam);
        Vec3d off = e.getLerpedPos(tickDelta).subtract(cam);
        Vec3d axis = e.getVelocity().lengthSquared() > 1e-6 ? e.getVelocity().normalize() : new Vec3d(0, 0, 1);
        Vec3d u = VfxDraw.perpendicular(axis), v = axis.crossProduct(u);

        matrices.push();
        matrices.translate(-off.x, -off.y, -off.z); // work in camera-relative space
        Matrix4f m = matrices.peek().getPositionMatrix();
        VertexConsumer glow = buffers.getBuffer(RenderLayer.getLightning());
        int core = style == VfxStyle.ENERGY ? 0xFFFFFF : style.core();
        int mid = style == VfxStyle.ENERGY ? color : style.mid();
        int outer = style == VfxStyle.ENERGY ? VfxDraw.lerpRgb(color, 0, 0.5f) : style.outer();
        float flicker = 0.85f + 0.15f * MathHelper.sin(time * 1.7f);
        // never glare into a camera the ball is leaving from: fade near the camera, grow the tail over the first ticks
        float near = (float) MathHelper.clamp((c.length() - R) / (R * 2 + 1.5), 0.0, 1.0);
        double tail = Math.min(1.0, time / 6.0);
        if (near <= 0.01f) { matrices.pop(); return; }

        switch (style) {
            case LIGHTNING -> lightning(glow, m, c, R, time, mid);
            case WIND -> windRings(glow, m, c, axis, u, v, R, time, mid);
            case EARTH, SAND, PLANT -> chunks(buffers.getBuffer(RenderLayer.getDebugQuads()), m, c, R, time, style);
            case SHADOW, INSECT -> darkMatter(buffers.getBuffer(RenderLayer.getDebugQuads()), m, c, axis, u, v, R, time, style);
            default -> { }
        }
        if (!StyleParticles.solid(style)) {
            // core + halo
            VfxDraw.disc(glow, m, c, R * 0.95 * flicker, VfxDraw.argb(mid, 0.55f * near), VfxDraw.argb(outer, 0f), 16);
            VfxDraw.disc(glow, m, c, R * 0.55, VfxDraw.argb(core, 0.95f * near), VfxDraw.argb(mid, 0.25f * near), 12);
            // spiral arms swirling in toward the ball from behind
            int arms = style == VfxStyle.WATER ? 5 : style == VfxStyle.WIND ? 3 : 4;
            int seg = 14;
            double spin = time * (style == VfxStyle.WIND ? 0.9 : 0.45);
            for (int a = 0; a < arms; a++) {
                Vec3d prev = null;
                double prevW = 0;
                int prevC = 0;
                for (int j = 0; j <= seg; j++) {
                    double t = j / (double) seg;                       // 0 = at the ball, 1 = tail end
                    double ang = a * Math.PI * 2 / arms + spin + t * Math.PI * 2.2;
                    double wobble = style == VfxStyle.WATER ? 1 + 0.18 * Math.sin(time * 0.6 + t * 9 + a) : 1;
                    double rad = R * (0.55 + 1.15 * t) * wobble;
                    Vec3d p = c.subtract(axis.multiply(t * R * 3.2 * tail))
                            .add(u.multiply(Math.cos(ang) * rad)).add(v.multiply(Math.sin(ang) * rad));
                    double w = R * 0.32 * (1 - t * 0.85);
                    int col = VfxDraw.argb(VfxDraw.lerpRgb(VfxDraw.lerpRgb(core, mid, (float) Math.min(1, t * 2)), outer,
                            (float) Math.max(0, t * 2 - 1)), (float) (0.85 * (1 - t) * near));
                    if (prev != null) VfxDraw.ribbon(glow, m, prev, p, prevW, w, prevC, col);
                    prev = p;
                    prevW = w;
                    prevC = col;
                }
            }
        }
        matrices.pop();
        super.render(e, yaw, tickDelta, matrices, buffers, light);
    }

    private static void lightning(VertexConsumer vc, Matrix4f m, Vec3d c, double R, float time, int mid) {
        Random r = Random.create((long) (time * 0.5f) * 31L + (long) (c.x * 7));
        for (int arc = 0; arc < 6; arc++) {
            Vec3d dir = new Vec3d(r.nextGaussian(), r.nextGaussian(), r.nextGaussian()).normalize();
            Vec3d p = c;
            int steps = 4;
            for (int i = 1; i <= steps; i++) {
                Vec3d q = c.add(dir.multiply(R * 1.6 * i / steps))
                        .add(new Vec3d(r.nextGaussian(), r.nextGaussian(), r.nextGaussian()).multiply(R * 0.18));
                VfxDraw.ribbon(vc, m, p, q, 0.035, 0.02, VfxDraw.argb(0xFFFFFF, 0.95f), VfxDraw.argb(mid, 0.6f));
                p = q;
            }
        }
    }

    private static void windRings(VertexConsumer vc, Matrix4f m, Vec3d c, Vec3d axis, Vec3d u, Vec3d v, double R, float time, int mid) {
        for (int i = 0; i < 3; i++) {
            double tilt = time * 0.8 + i * Math.PI / 3;
            Vec3d ru = u.multiply(Math.cos(tilt)).add(axis.multiply(Math.sin(tilt)));
            VfxDraw.ring(vc, m, c, ru, v, R * (1.05 + 0.1 * i), R * 0.12, VfxDraw.argb(mid, 0.55f), VfxDraw.argb(0xFFFFFF, 0.1f), 20);
        }
    }

    private static void chunks(VertexConsumer vc, Matrix4f m, Vec3d c, double R, float time, VfxStyle style) {
        Random r = Random.create(42);
        int n = 7;
        for (int i = 0; i < n; i++) {
            double a = i * Math.PI * 2 / n + time * 0.25;
            double el = (r.nextDouble() - 0.5) * 1.6;
            Vec3d p = c.add(new Vec3d(Math.cos(a) * Math.cos(el), Math.sin(el), Math.sin(a) * Math.cos(el)).multiply(R * 0.65));
            int rgb = i % 2 == 0 ? style.mid() : style.outer();
            VfxDraw.cube(vc, m, p, R * (0.22 + r.nextDouble() * 0.12), time * 0.2 + i, time * 0.31 + i * 2, rgb, 1f);
        }
        VfxDraw.cube(vc, m, c, R * 0.42, time * 0.15, time * 0.2, style.mid(), 1f);
    }

    private static void darkMatter(VertexConsumer vc, Matrix4f m, Vec3d c, Vec3d axis, Vec3d u, Vec3d v, double R, float time, VfxStyle style) {
        for (int i = 0; i < 3; i++) {
            double tilt = time * 0.35 + i * Math.PI / 3;
            Vec3d ru = u.multiply(Math.cos(tilt)).add(axis.multiply(Math.sin(tilt)));
            VfxDraw.ring(vc, m, c, ru, v, R * 0.8, R * 0.5, VfxDraw.argb(style.outer(), 0.85f), VfxDraw.argb(style.mid(), 0.25f), 18);
        }
        VfxDraw.cube(vc, m, c, R * 0.35, time * 0.3, time * 0.4, style.outer(), 0.95f);
    }
}
