package com.animecore.client.vfx;

import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.entity.EnergyProjectileEntity;
import com.animecore.vfx.ScreenEffectPayload;
import com.animecore.vfx.VfxKind;
import com.animecore.vfx.VfxPayload;
import com.animecore.vfx.VfxStyle;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Client half of the technique VFX pipeline. Each {@link VfxPayload} becomes a short-lived effect that spawns
 * element particles every tick and draws procedural glow geometry every frame; projectiles get a converging elemental
 * trail. Honours the vanilla particle setting. Pure client code (client source set).
 */
public final class VfxManager {
    private VfxManager() {}

    private static final List<Effect> EFFECTS = new ArrayList<>();
    private static final int MAX_EFFECTS = 256;

    private static final class Effect {
        final VfxKind kind;
        final VfxStyle style;
        final int color;
        final int entityId;
        final Vec3d origin, dir;
        final float size, extra;
        final int life;
        int age;

        Effect(VfxPayload p) {
            kind = p.kind();
            style = p.style();
            color = p.color();
            entityId = p.entityId();
            origin = p.origin();
            dir = p.dir();
            size = p.size();
            extra = p.extra();
            life = switch (kind) {
                case CAST -> 8;
                case BEAM -> 12;
                case SHOCKWAVE -> 14;
                case AREA -> 22;
                case WALL -> 16;
                case BUFF -> 22;
                case DASH -> 10;
                case IMPACT -> 10;
                case MARK -> Math.max(20, (int) size);
            };
        }

        float progress(float delta) { return MathHelper.clamp((age + delta) / life, 0f, 1f); }

        int mid() { return style == VfxStyle.ENERGY ? color : style.mid(); }
        int core() { return style == VfxStyle.ENERGY ? 0xFFFFFF : style.core(); }
        int outer() { return style == VfxStyle.ENERGY ? VfxDraw.lerpRgb(color, 0, 0.5f) : style.outer(); }
    }

    public static void init() {
        AnimeClientNetworking.registerReceiver(VfxPayload.ID, (payload, client) -> {
            if (EFFECTS.size() >= MAX_EFFECTS) EFFECTS.remove(0);
            EFFECTS.add(new Effect(payload));
        });
        AnimeClientNetworking.registerReceiver(ScreenEffectPayload.ID, (payload, client) -> ScreenEffectOverlay.start(payload));
        ClientTickEvents.END_WORLD_TICK.register(VfxManager::tick);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx -> render(ctx.matrixStack(), ctx.tickCounter().getTickDelta(false)));
        HudRenderCallback.EVENT.register(ScreenEffectOverlay::render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> ScreenEffectOverlay.tick(client));
    }

    // ------------------------------------------------------------------ particles (per tick)

    private static float budget() {
        ParticlesMode mode = MinecraftClient.getInstance().options.getParticles().getValue();
        return mode == ParticlesMode.ALL ? 1f : mode == ParticlesMode.DECREASED ? 0.45f : 0.15f;
    }

    private static void tick(ClientWorld world) {
        float budget = budget();
        Random r = world.getRandom();
        Iterator<Effect> it = EFFECTS.iterator();
        while (it.hasNext()) {
            Effect e = it.next();
            if (e.age++ >= e.life) {
                it.remove();
                continue;
            }
            particles(world, e, r, budget);
        }
        tickProjectiles(world, r, budget);
    }

    private static int n(float base, float budget, Random r) {
        float v = base * budget;
        int i = (int) v;
        return i + (r.nextFloat() < v - i ? 1 : 0);
    }

    private static void add(ClientWorld w, ParticleEffect p, Vec3d at, Vec3d vel) {
        w.addParticle(p, at.x, at.y, at.z, vel.x, vel.y, vel.z);
    }

    private static Vec3d rnd(Random r) {
        return new Vec3d(r.nextGaussian(), r.nextGaussian(), r.nextGaussian()).normalize();
    }

    private static void particles(ClientWorld w, Effect e, Random r, float budget) {
        VfxStyle s = e.style;
        switch (e.kind) {
            case CAST -> {
                // energy pulled into the hands from all around
                for (int i = 0; i < n(6 + e.size * 4, budget, r); i++) {
                    Vec3d d = rnd(r);
                    Vec3d from = e.origin.add(d.multiply(1.1 + r.nextDouble() * 0.5));
                    add(w, StyleParticles.glow(s, e.color, r, 0.8f), from, d.multiply(-0.12));
                }
                if (e.age == 1) for (int i = 0; i < n(5, budget, r); i++) add(w, StyleParticles.primary(s, r), e.origin, rnd(r).multiply(0.05));
            }
            case BEAM -> {
                if (e.age > 6) return;
                Vec3d d = e.dir.normalize();
                for (int i = 0; i < n(e.size * 1.5f, budget, r); i++) {
                    double t = r.nextDouble() * e.size;
                    Vec3d p = e.origin.add(d.multiply(t)).add(rnd(r).multiply(e.extra * 0.4));
                    add(w, r.nextBoolean() ? StyleParticles.primary(s, r) : StyleParticles.glow(s, e.color, r, 0.9f), p, rnd(r).multiply(0.03));
                }
            }
            case SHOCKWAVE -> {
                float rad = e.size * easeOut(e.progress(0));
                for (int i = 0; i < n(18 + e.size * 6, budget, r); i++) {
                    double a = r.nextDouble() * Math.PI * 2;
                    Vec3d dir = new Vec3d(Math.cos(a), 0, Math.sin(a));
                    add(w, i % 7 == 0 ? StyleParticles.accent(s, r) : StyleParticles.primary(s, r),
                            e.origin.add(dir.multiply(rad)).add(0, -0.8 + r.nextDouble() * 0.6, 0), dir.multiply(0.12).add(0, 0.05, 0));
                }
            }
            case AREA -> {
                float p = e.progress(0);
                double half = Math.toRadians(e.extra / 2);
                Vec3d look = new Vec3d(e.dir.x, 0, e.dir.z);
                double baseYaw = look.lengthSquared() > 1e-4 ? Math.atan2(look.z, look.x) : 0;
                for (int i = 0; i < n(14 + e.size * 2, budget, r); i++) {
                    // spiral flow outward through the cone / sphere
                    double t = r.nextDouble();
                    double ang = e.extra >= 360 ? r.nextDouble() * Math.PI * 2 : baseYaw + (r.nextDouble() * 2 - 1) * half;
                    ang += (t * 2.5 + e.age * 0.2) * (e.extra >= 360 ? 1 : 0.15);
                    double dist = e.size * Math.min(1, p * 1.4) * t;
                    Vec3d at = e.origin.add(Math.cos(ang) * dist, -0.6 + Math.sin(t * 9 + e.age) * 0.4, Math.sin(ang) * dist);
                    add(w, i % 2 == 0 ? StyleParticles.primary(s, r) : StyleParticles.glow(s, e.color, r, 1f), at,
                            new Vec3d(Math.cos(ang), 0.02, Math.sin(ang)).multiply(0.05));
                }
            }
            case WALL -> {
                if (e.age > 8) return;
                Vec3d axis = e.dir.lengthSquared() > 1e-4 ? e.dir.normalize() : new Vec3d(1, 0, 0);
                var block = new net.minecraft.particle.BlockStateParticleEffect(ParticleTypes.BLOCK, StyleParticles.wallBlock(s));
                for (int i = 0; i < n(20 + e.size * 4, budget, r); i++) {
                    double t = (r.nextDouble() - 0.5) * e.size;
                    Vec3d at = e.origin.add(axis.multiply(t)).add(0, r.nextDouble() * e.extra * (e.age / 8.0), 0);
                    add(w, r.nextFloat() < 0.6f ? block : StyleParticles.primary(s, r), at, new Vec3d(0, 0.2 + r.nextDouble() * 0.2, 0));
                    if (i % 4 == 0) add(w, ParticleTypes.POOF, e.origin.add(axis.multiply(t)), rnd(r).multiply(0.06));
                }
            }
            case BUFF -> {
                Entity ent = w.getEntityById(e.entityId);
                Vec3d base = ent != null ? ent.getPos() : e.origin;
                for (int i = 0; i < n(6 + e.size * 4, budget, r); i++) {
                    double a = r.nextDouble() * Math.PI * 2;
                    double rad = 0.5 + r.nextDouble() * 0.4;
                    add(w, i % 2 == 0 ? StyleParticles.glow(s, e.color, r, 1.1f) : StyleParticles.primary(s, r),
                            base.add(Math.cos(a) * rad, r.nextDouble() * 0.3, Math.sin(a) * rad), new Vec3d(0, 0.18 + r.nextDouble() * 0.15, 0));
                }
                if (e.extra > 0 && e.age % 3 == 0) add(w, ParticleTypes.HEART, base.add(0, 2.1, 0), Vec3d.ZERO);
            }
            case DASH -> {
                if (e.age > 4) return;
                for (int i = 0; i < n(e.size * 3, budget, r); i++) {
                    Vec3d at = e.origin.add(e.dir.multiply(r.nextDouble() * e.size)).add(rnd(r).multiply(0.35));
                    add(w, i % 2 == 0 ? StyleParticles.primary(s, r) : ParticleTypes.CLOUD, at, Vec3d.ZERO);
                }
            }
            case IMPACT -> {
                if (e.age > 2) return;
                int count = n(14 + e.size * 10, budget, r);
                for (int i = 0; i < count; i++) {
                    Vec3d d = rnd(r);
                    add(w, i % 5 == 0 ? StyleParticles.accent(s, r) : StyleParticles.primary(s, r), e.origin,
                            d.multiply(0.15 + e.size * 0.08 * r.nextDouble()));
                }
                if (e.age == 1 && e.size >= 1.5f) add(w, ParticleTypes.FLASH, e.origin, Vec3d.ZERO);
            }
            case MARK -> {
                Entity target = w.getEntityById((int) e.extra);
                if (target == null || e.age % 4 != 0) return;
                Vec3d head = target.getPos().add(0, target.getHeight() + 0.3, 0);
                add(w, StyleParticles.primary(s, r), head.add(rnd(r).multiply(0.4)), new Vec3d(0, 0.02, 0));
            }
        }
    }

    /** Elemental trail spiralling into every technique ball in flight. */
    static void tickProjectiles(ClientWorld w, Random r, float budget) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        for (Entity ent : w.getEntities()) {
            if (!(ent instanceof EnergyProjectileEntity p) || ent.squaredDistanceTo(client.player) > 96 * 96) continue;
            VfxStyle s = p.getStyle();
            float size = p.getSize();
            Vec3d c = p.getPos().add(0, size / 2, 0);
            Vec3d vel = p.getVelocity();
            Vec3d axis = vel.lengthSquared() > 1e-6 ? vel.normalize() : new Vec3d(0, 0, 1);
            Vec3d u = VfxDraw.perpendicular(axis), v = axis.crossProduct(u);
            int count = n(3 + size * 4, budget, r);
            for (int i = 0; i < count; i++) {
                double ang = r.nextDouble() * Math.PI * 2;
                double rad = size * (0.8 + r.nextDouble() * 0.6);
                Vec3d radial = u.multiply(Math.cos(ang)).add(v.multiply(Math.sin(ang)));
                Vec3d at = c.subtract(axis.multiply(size * (0.4 + r.nextDouble()))).add(radial.multiply(rad));
                // swirl tangentially and fall inward toward the ball while it races ahead
                Vec3d tangent = axis.crossProduct(radial);
                Vec3d pv = vel.multiply(0.6).add(radial.multiply(-0.08)).add(tangent.multiply(0.08));
                add(w, i % 3 == 2 ? StyleParticles.glow(s, p.getColor(), r, Math.min(3f, 0.8f + size)) : StyleParticles.primary(s, r), at, pv);
            }
            if ((s == VfxStyle.FLAME || s == VfxStyle.LAVA) && r.nextFloat() < 0.4f * budget) {
                add(w, ParticleTypes.SMOKE, c.subtract(axis.multiply(size * 1.5)), Vec3d.ZERO);
            }
        }
    }

    // ------------------------------------------------------------------ geometry (per frame)

    private static float easeOut(float t) { return 1 - (1 - t) * (1 - t); }

    private static void render(MatrixStack matrices, float delta) {
        if (EFFECTS.isEmpty()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        Vec3d cam = client.gameRenderer.getCamera().getPos();
        VertexConsumerProvider.Immediate buffers = client.getBufferBuilders().getEffectVertexConsumers();
        Matrix4f m = matrices.peek().getPositionMatrix();
        VertexConsumer glow = buffers.getBuffer(RenderLayer.getLightning());
        for (Effect e : EFFECTS) {
            try {
                draw(client, glow, buffers, m, e, cam, delta);
            } catch (RuntimeException ignored) {
                // never let a cosmetic effect crash the frame
            }
        }
        buffers.draw();
    }

    private static void draw(MinecraftClient client, VertexConsumer vc, VertexConsumerProvider.Immediate buffers, Matrix4f m,
                             Effect e, Vec3d cam, float delta) {
        float p = e.progress(delta);
        float fade = 1 - p;
        Vec3d o = e.origin.subtract(cam);
        Vec3d up = new Vec3d(0, 1, 0), ex = new Vec3d(1, 0, 0), ez = new Vec3d(0, 0, 1);
        switch (e.kind) {
            case CAST -> {
                // the caster's own first-person camera sits right behind this point: particles only, no glare
                if (client.player != null && e.entityId == client.player.getId() && client.options.getPerspective().isFirstPerson()) return;
                double r = (0.25 + e.size * 0.35) * (p < 0.4 ? p / 0.4 : 1);
                VfxDraw.disc(vc, m, o, r, VfxDraw.argb(e.core(), 0.9f * fade), VfxDraw.argb(e.mid(), 0f), 12);
            }
            case BEAM -> {
                Vec3d d = e.dir.normalize();
                double width = Math.max(0.1, e.extra) * (p < 0.2 ? p / 0.2 : 1);
                int segs = Math.max(2, (int) (e.size * 1.5));
                Random jr = Random.create(e.age * 7919L + e.entityId);
                Vec3d prev = o;
                Vec3d u = VfxDraw.perpendicular(d), v = d.crossProduct(u);
                for (int i = 1; i <= segs; i++) {
                    double t = i / (double) segs;
                    Vec3d q = o.add(d.multiply(t * e.size));
                    if (e.style == VfxStyle.LIGHTNING && i < segs) q = q.add(u.multiply(jr.nextGaussian() * 0.25)).add(v.multiply(jr.nextGaussian() * 0.25));
                    VfxDraw.ribbon(vc, m, prev, q, width, width, VfxDraw.argb(e.mid(), 0.6f * fade), VfxDraw.argb(e.mid(), 0.6f * fade));
                    VfxDraw.ribbon(vc, m, prev, q, width * 0.35, width * 0.35, VfxDraw.argb(e.core(), 0.95f * fade), VfxDraw.argb(e.core(), 0.95f * fade));
                    prev = q;
                }
                if (e.style != VfxStyle.LIGHTNING) {
                    // two helix strands wrapping the beam
                    for (int strand = 0; strand < 2; strand++) {
                        Vec3d hp = null;
                        for (int i = 0; i <= segs * 3; i++) {
                            double t = i / (double) (segs * 3);
                            double ang = strand * Math.PI + t * e.size * 2.2 + (e.age + delta) * 0.6;
                            Vec3d q = o.add(d.multiply(t * e.size)).add(u.multiply(Math.cos(ang) * width * 1.6)).add(v.multiply(Math.sin(ang) * width * 1.6));
                            if (hp != null) VfxDraw.ribbon(vc, m, hp, q, width * 0.18, width * 0.18,
                                    VfxDraw.argb(e.outer(), 0.7f * fade), VfxDraw.argb(e.outer(), 0.7f * fade));
                            hp = q;
                        }
                    }
                }
            }
            case SHOCKWAVE -> {
                double r = e.size * easeOut(p);
                Vec3d c = o.add(0, -0.85, 0);
                VfxDraw.ring(vc, m, c, ex, ez, r, 0.35 + e.size * 0.12, VfxDraw.argb(e.core(), 0.8f * fade), VfxDraw.argb(e.mid(), 0f), 36);
                VfxDraw.ring(vc, m, c.add(0, 0.02, 0), ex, ez, r, 0.18 + e.size * 0.06, VfxDraw.argb(e.mid(), 0.9f * fade), VfxDraw.argb(e.mid(), 0.9f * fade), 36);
                // short glowing wall of the wave front
                for (int i = 0; i < 36; i++) {
                    double a0 = Math.PI * 2 * i / 36, a1 = Math.PI * 2 * (i + 1) / 36;
                    Vec3d p0 = c.add(Math.cos(a0) * r, 0, Math.sin(a0) * r), p1 = c.add(Math.cos(a1) * r, 0, Math.sin(a1) * r);
                    double hh = 0.9 * fade;
                    VfxDraw.quad2(vc, m, p0, p1, p1.add(0, hh, 0), p0.add(0, hh, 0), VfxDraw.argb(e.mid(), 0.55f * fade),
                            VfxDraw.argb(e.mid(), 0.55f * fade), VfxDraw.argb(e.core(), 0f), VfxDraw.argb(e.core(), 0f));
                }
                VfxDraw.ring(vc, m, c, ex, ez, r * 0.7, 0.2, VfxDraw.argb(e.mid(), 0.5f * fade), VfxDraw.argb(e.outer(), 0f), 30);
                VfxDraw.disc(vc, m, o, r * 0.5 * fade, VfxDraw.argb(e.core(), 0.5f * fade), VfxDraw.argb(e.mid(), 0f), 14);
            }
            case AREA -> runeCircle(vc, m, o.add(0, -0.85, 0), e, p, delta);
            case WALL -> {
                Vec3d axis = e.dir.lengthSquared() > 1e-4 ? e.dir.normalize() : ex;
                double h = e.extra * easeOut(Math.min(1, p * 2));
                Vec3d a = o.add(axis.multiply(-e.size / 2)), b = o.add(axis.multiply(e.size / 2));
                VfxDraw.quad2(vc, m, a, b, b.add(0, h, 0), a.add(0, h, 0),
                        VfxDraw.argb(e.mid(), 0.45f * fade), VfxDraw.argb(e.mid(), 0.45f * fade), VfxDraw.argb(e.core(), 0f), VfxDraw.argb(e.core(), 0f));
            }
            case BUFF -> {
                Entity ent = client.world == null ? null : client.world.getEntityById(e.entityId);
                Vec3d base = (ent != null ? ent.getLerpedPos(delta) : e.origin).subtract(cam);
                float a = (p < 0.2f ? p / 0.2f : fade);
                VfxDraw.ring(vc, m, base.add(0, 0.05, 0), ex, ez, 0.6 + p * 0.8, 0.25, VfxDraw.argb(e.core(), 0.7f * a), VfxDraw.argb(e.mid(), 0f), 28);
                for (int strand = 0; strand < 3; strand++) {
                    Vec3d hp = null;
                    for (int i = 0; i <= 24; i++) {
                        double t = i / 24.0;
                        double ang = strand * Math.PI * 2 / 3 + t * Math.PI * 3 + (e.age + delta) * 0.35;
                        double rad = 0.55 - t * 0.2;
                        Vec3d q = base.add(Math.cos(ang) * rad, t * 2.2 * Math.min(1, p * 3), Math.sin(ang) * rad);
                        int col = VfxDraw.argb(VfxDraw.lerpRgb(e.mid(), e.core(), (float) t), (float) (0.75 * a * (1 - t * 0.6)));
                        if (hp != null) VfxDraw.ribbon(vc, m, hp, q, 0.06, 0.06, col, col);
                        hp = q;
                    }
                }
            }
            case DASH -> {
                Vec3d end = o.add(e.dir.multiply(e.size));
                VfxDraw.ribbon(vc, m, o, end, 0.05, 0.45 * fade, VfxDraw.argb(e.mid(), 0f), VfxDraw.argb(e.core(), 0.7f * fade));
                VfxDraw.ribbon(vc, m, o.add(0, -0.4, 0), end.add(0, -0.4, 0), 0.03, 0.3 * fade, VfxDraw.argb(e.outer(), 0f), VfxDraw.argb(e.mid(), 0.5f * fade));
            }
            case IMPACT -> {
                double r = e.size * (0.4 + 0.8 * easeOut(p));
                VfxDraw.disc(vc, m, o, r, VfxDraw.argb(e.core(), 0.9f * fade), VfxDraw.argb(e.mid(), 0f), 16);
                Random sr = Random.create(e.entityId * 31L + (long) (e.origin.x * 13));
                for (int i = 0; i < 8; i++) {
                    Vec3d d = new Vec3d(sr.nextGaussian(), sr.nextGaussian(), sr.nextGaussian()).normalize();
                    VfxDraw.ribbon(vc, m, o, o.add(d.multiply(r * 1.6)), 0.12 * e.size * fade, 0.0, VfxDraw.argb(e.core(), 0.8f * fade), VfxDraw.argb(e.mid(), 0f));
                }
            }
            case MARK -> {
                Entity target = client.world == null ? null : client.world.getEntityById((int) e.extra);
                if (target == null || !target.isAlive()) return;
                Vec3d head = target.getLerpedPos(delta).add(0, target.getHeight() + 0.45, 0).subtract(cam);
                float a = Math.min(1f, Math.min(p * 8, (1 - p) * 4));
                double spin = (e.age + delta) * 0.25;
                VfxDraw.ring(vc, m, head, ex, ez, 0.45, 0.06, VfxDraw.argb(e.mid(), 0.85f * a), VfxDraw.argb(e.mid(), 0.85f * a), 24);
                for (int k = 0; k < 3; k++) {   // three rotating commas (tomoe-like) - a generic "caught" sigil
                    double ang = spin + k * Math.PI * 2 / 3;
                    Vec3d dot = head.add(Math.cos(ang) * 0.45, 0, Math.sin(ang) * 0.45);
                    VfxDraw.disc(vc, m, dot, 0.09, VfxDraw.argb(e.core(), a), VfxDraw.argb(e.outer(), 0.4f * a), 8);
                    Vec3d tail = head.add(Math.cos(ang - 0.6) * 0.38, 0, Math.sin(ang - 0.6) * 0.38);
                    VfxDraw.ribbon(vc, m, dot, tail, 0.05, 0.0, VfxDraw.argb(e.core(), a), VfxDraw.argb(e.outer(), 0f));
                }
            }
        }
    }

    /** Ground sigil for area techniques: rotating double ring with spokes, plus a sweeping cone edge. */
    private static void runeCircle(VertexConsumer vc, Matrix4f m, Vec3d c, Effect e, float p, float delta) {
        Vec3d ex = new Vec3d(1, 0, 0), ez = new Vec3d(0, 0, 1);
        float a = Math.min(1f, Math.min(p * 5, (1 - p) * 3));
        double r = Math.min(4.5, Math.max(1.5, e.size * 0.45)) * (0.6 + 0.4 * easeOut(Math.min(1, p * 2)));
        double spin = (e.age + delta) * 0.08;
        Vec3d u = ex.multiply(Math.cos(spin)).add(ez.multiply(Math.sin(spin)));
        Vec3d v = new Vec3d(-u.z, 0, u.x);
        VfxDraw.ring(vc, m, c, u, v, r, 0.12, VfxDraw.argb(e.mid(), 0.85f * a), VfxDraw.argb(e.core(), 0.6f * a), 40);
        VfxDraw.ring(vc, m, c, u, v, r * 0.72, 0.08, VfxDraw.argb(e.core(), 0.7f * a), VfxDraw.argb(e.mid(), 0.5f * a), 32);
        int spokes = e.style == VfxStyle.ILLUSION ? 3 : 6;
        for (int i = 0; i < spokes; i++) {
            double ang = i * Math.PI * 2 / spokes;
            Vec3d d = u.multiply(Math.cos(ang)).add(v.multiply(Math.sin(ang)));
            Vec3d d2 = u.multiply(Math.cos(ang + 0.7)).add(v.multiply(Math.sin(ang + 0.7)));
            VfxDraw.quad2(vc, m, c.add(d.multiply(r * 0.72)), c.add(d.multiply(r)), c.add(d2.multiply(r)), c.add(d2.multiply(r * 0.86)),
                    VfxDraw.argb(e.mid(), 0.5f * a), VfxDraw.argb(e.core(), 0.6f * a), VfxDraw.argb(e.mid(), 0.1f * a), VfxDraw.argb(e.mid(), 0.1f * a));
        }
        if (e.extra < 360) { // cone wedge sweeping out toward the targets
            Vec3d look = new Vec3d(e.dir.x, 0, e.dir.z);
            if (look.lengthSquared() < 1e-4) return;
            double yaw = Math.atan2(look.z, look.x), half = Math.toRadians(e.extra / 2), reach = e.size * easeOut(Math.min(1f, p * 1.5f));
            int segs = 12;
            for (int i = 0; i < segs; i++) {
                double a0 = yaw - half + 2 * half * i / segs, a1 = yaw - half + 2 * half * (i + 1) / segs;
                Vec3d p0 = c.add(Math.cos(a0) * reach, 0.05, Math.sin(a0) * reach), p1 = c.add(Math.cos(a1) * reach, 0.05, Math.sin(a1) * reach);
                VfxDraw.quad2(vc, m, c, p0, p1, c, VfxDraw.argb(e.mid(), 0.35f * a), VfxDraw.argb(e.mid(), 0f), VfxDraw.argb(e.mid(), 0f),
                        VfxDraw.argb(e.mid(), 0.35f * a));
            }
        }
    }
}
