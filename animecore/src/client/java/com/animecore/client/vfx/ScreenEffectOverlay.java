package com.animecore.client.vfx;

import com.animecore.vfx.ScreenEffect;
import com.animecore.vfx.ScreenEffectPayload;
import com.animecore.vfx.VfxStyle;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;

/**
 * Full-screen technique effects drawn on the HUD with procedural geometry (no textures, no shaders, so it works with
 * every shader pack / Essential setup):
 * <ul>
 *   <li>{@link ScreenEffect#ILLUSION} - genjutsu vision: tinted pulsing world, coloured vignette, a huge slowly rotating
 *       three-comma spiral, expanding ripples, and petals / sparks drifting around the victim in the world.</li>
 *   <li>{@link ScreenEffect#DARKNESS} - shadow binding: wavy black tendrils closing in from the edges.</li>
 *   <li>{@link ScreenEffect#FLASH} - blinding light fading out.</li>
 * </ul>
 */
public final class ScreenEffectOverlay {
    private ScreenEffectOverlay() {}

    private static ScreenEffect effect = ScreenEffect.NONE;
    private static VfxStyle style = VfxStyle.ILLUSION;
    private static int color = 0xC2185B;
    private static int total, remaining;
    private static com.animecore.vfx.IllusionProfile profile = com.animecore.vfx.IllusionProfile.DEFAULT;

    public static void start(ScreenEffectPayload p) {
        effect = p.effect();
        style = p.style();
        color = p.color();
        total = remaining = Math.max(10, p.ticks());
        profile = p.profile();
    }

    public static boolean active() { return remaining > 0 && effect != ScreenEffect.NONE; }

    static void tick(MinecraftClient client) {
        if (!active()) return;
        remaining--;
        if (effect == ScreenEffect.ILLUSION && client.player != null && client.world != null) {
            Random r = client.world.getRandom();
            var pos = client.player.getPos();
            for (int i = 0; i < 2; i++) {
                client.world.addParticle(IllusionRenderer.particle(profile),
                        pos.x + (r.nextDouble() - 0.5) * 6, pos.y + 1 + r.nextDouble() * 2.5, pos.z + (r.nextDouble() - 0.5) * 6, 0, -0.02, 0);
            }
        }
    }

    /** Envelope: fade in over 10 ticks, fade out over the last 20. */
    private static float envelope(float delta) {
        float elapsed = total - remaining + delta;
        float left = remaining - delta;
        return MathHelper.clamp(Math.min(elapsed / 10f, left / 20f), 0f, 1f);
    }

    static void render(DrawContext ctx, RenderTickCounter counter) {
        if (!active()) return;
        if (effect == ScreenEffect.TIME_STOP) {
            timeStop(ctx, counter.getTickDelta(false));
            return;
        }
        float delta = counter.getTickDelta(false);
        float env = envelope(delta);
        if (env <= 0f) return;
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();
        float time = (total - remaining) + delta;
        Matrix4f m = ctx.getMatrices().peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull(); // screen-space triangles are wound both ways
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferBuilder b = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        float cx = w / 2f, cy = h / 2f, R = (float) Math.hypot(cx, cy);
        switch (effect) {
            case ILLUSION -> IllusionRenderer.screen(b, m, profile, w, h, time, env);
            case DARKNESS -> darkness(b, m, cx, cy, R, time, env);
            case FLASH -> rect(b, m, 0, 0, w, h, VfxDraw.argb(0xFFFFFF, 0.9f * env * env));
            default -> { }
        }
        BuiltBuffer built = b.endNullable();
        if (built != null) BufferRenderer.drawWithGlobalProgram(built);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    /**
     * Stopped time: the world flashes into its colour negative as the stop spreads, then stays drained and cold
     * (blue-grey tint, dark vignette) until time resumes; the colours snap back over the last few ticks.
     */
    private static void timeStop(DrawContext ctx, float delta) {
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();
        float elapsed = (total - remaining) + delta, left = remaining - delta;
        Matrix4f m = ctx.getMatrices().peek().getPositionMatrix();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        if (elapsed < 8f || left < 3f) {   // negative flash: 1 - dst
            RenderSystem.blendFuncSeparate(com.mojang.blaze3d.platform.GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR,
                    com.mojang.blaze3d.platform.GlStateManager.DstFactor.ZERO,
                    com.mojang.blaze3d.platform.GlStateManager.SrcFactor.ONE, com.mojang.blaze3d.platform.GlStateManager.DstFactor.ZERO);
            BufferBuilder inv = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
            float cx = w / 2f, cy = h / 2f, R = (float) Math.hypot(cx, cy);
            float spread = left < 3f ? 1f : MathHelper.clamp(elapsed / 5f, 0.05f, 1f);   // the negative sphere expands
            radial(inv, m, cx, cy, 0f, R * spread * 1.05f, 0xFFFFFFFF, 0xFFFFFFFF, 48, 0, 0);
            BuiltBuffer built = inv.endNullable();
            if (built != null) BufferRenderer.drawWithGlobalProgram(built);
        }
        RenderSystem.defaultBlendFunc();
        float env = MathHelper.clamp(Math.min((elapsed - 6f) / 4f, left / 3f), 0f, 1f);
        if (env > 0f) {
            BufferBuilder b = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
            rect(b, m, 0, 0, w, h, VfxDraw.argb(0x4A5470, 0.38f * env));
            float cx = w / 2f, cy = h / 2f, R = (float) Math.hypot(cx, cy);
            radial(b, m, cx, cy, R * 0.5f, R * 1.05f, VfxDraw.argb(0x10101A, 0f), VfxDraw.argb(0x10101A, 0.6f * env), 48, 0, 0);
            BuiltBuffer built = b.endNullable();
            if (built != null) BufferRenderer.drawWithGlobalProgram(built);
        }
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void illusion(BufferBuilder b, Matrix4f m, float cx, float cy, float R, int w, int h, float time, float env) {
        int tint = VfxDraw.lerpRgb(color, 0xFFFFFF, 0.1f);
        float pulse = 0.5f + 0.5f * MathHelper.sin(time * 0.15f);
        rect(b, m, 0, 0, w, h, VfxDraw.argb(tint, (0.10f + 0.08f * pulse) * env));
        // vignette: transparent centre -> coloured edge
        radial(b, m, cx, cy, R * 0.45f, R * 1.05f, VfxDraw.argb(color, 0f), VfxDraw.argb(VfxDraw.lerpRgb(color, 0, 0.45f), 0.75f * env), 48, 0, 0);
        // three rotating commas (spiral arms)
        float spin = time * 0.03f;
        for (int k = 0; k < 3; k++) {
            float base = spin + k * MathHelper.TAU / 3;
            int steps = 26;
            for (int i = 0; i < steps; i++) {
                float t0 = i / (float) steps, t1 = (i + 1) / (float) steps;
                float a0 = base + t0 * 2.4f, a1 = base + t1 * 2.4f;
                float r0 = R * (0.08f + 0.55f * t0), r1 = R * (0.08f + 0.55f * t1);
                float w0 = R * 0.07f * (1 - t0) + 2, w1 = R * 0.07f * (1 - t1) + 2;
                int c0 = VfxDraw.argb(color, 0.32f * env * (1 - t0 * 0.7f)), c1 = VfxDraw.argb(color, 0.32f * env * (1 - t1 * 0.7f));
                float x0 = cx + MathHelper.cos(a0) * r0, y0 = cy + MathHelper.sin(a0) * r0;
                float x1 = cx + MathHelper.cos(a1) * r1, y1 = cy + MathHelper.sin(a1) * r1;
                float nx0 = MathHelper.cos(a0), ny0 = MathHelper.sin(a0), nx1 = MathHelper.cos(a1), ny1 = MathHelper.sin(a1);
                tri(b, m, x0 - nx0 * w0, y0 - ny0 * w0, x0 + nx0 * w0, y0 + ny0 * w0, x1 + nx1 * w1, y1 + ny1 * w1, c0, c0, c1);
                tri(b, m, x0 - nx0 * w0, y0 - ny0 * w0, x1 + nx1 * w1, y1 + ny1 * w1, x1 - nx1 * w1, y1 - ny1 * w1, c0, c1, c1);
            }
        }
        // ripples expanding from the centre
        for (int k = 0; k < 3; k++) {
            float ph = ((time * 0.02f) + k / 3f) % 1f;
            float rr = R * ph;
            radial(b, m, cx, cy, rr, rr + 3 + 6 * ph, VfxDraw.argb(0xFFFFFF, 0.25f * env * (1 - ph)), VfxDraw.argb(color, 0f), 48, 0, 0);
        }
    }

    private static void darkness(BufferBuilder b, Matrix4f m, float cx, float cy, float R, float time, float env) {
        // tendrils: radius varies with angle and time, closing in as the effect peaks
        int segs = 64;
        float inner = R * (1.0f - 0.55f * env);
        for (int i = 0; i < segs; i++) {
            float a0 = MathHelper.TAU * i / segs, a1 = MathHelper.TAU * (i + 1) / segs;
            float w0 = 1 + 0.18f * MathHelper.sin(a0 * 7 + time * 0.2f) + 0.1f * MathHelper.sin(a0 * 13 - time * 0.33f);
            float w1 = 1 + 0.18f * MathHelper.sin(a1 * 7 + time * 0.2f) + 0.1f * MathHelper.sin(a1 * 13 - time * 0.33f);
            float ri0 = inner * w0, ri1 = inner * w1, ro = R * 1.2f;
            int edge = VfxDraw.argb(0x05000A, 0.92f * env), soft = VfxDraw.argb(0x1A0026, 0f);
            float ix0 = cx + MathHelper.cos(a0) * ri0, iy0 = cy + MathHelper.sin(a0) * ri0;
            float ix1 = cx + MathHelper.cos(a1) * ri1, iy1 = cy + MathHelper.sin(a1) * ri1;
            float sx0 = cx + MathHelper.cos(a0) * ri0 * 0.8f, sy0 = cy + MathHelper.sin(a0) * ri0 * 0.8f;
            float sx1 = cx + MathHelper.cos(a1) * ri1 * 0.8f, sy1 = cy + MathHelper.sin(a1) * ri1 * 0.8f;
            float ox0 = cx + MathHelper.cos(a0) * ro, oy0 = cy + MathHelper.sin(a0) * ro;
            float ox1 = cx + MathHelper.cos(a1) * ro, oy1 = cy + MathHelper.sin(a1) * ro;
            tri(b, m, ix0, iy0, ox0, oy0, ox1, oy1, edge, edge, edge);
            tri(b, m, ix0, iy0, ox1, oy1, ix1, iy1, edge, edge, edge);
            tri(b, m, sx0, sy0, ix0, iy0, ix1, iy1, soft, edge, edge);
            tri(b, m, sx0, sy0, ix1, iy1, sx1, sy1, soft, edge, soft);
        }
    }

    // ---- 2D helpers ----

    private static void tri(BufferBuilder b, Matrix4f m, float x0, float y0, float x1, float y1, float x2, float y2, int c0, int c1, int c2) {
        b.vertex(m, x0, y0, 0).color(c0);
        b.vertex(m, x1, y1, 0).color(c1);
        b.vertex(m, x2, y2, 0).color(c2);
    }

    private static void rect(BufferBuilder b, Matrix4f m, float x0, float y0, float x1, float y1, int c) {
        tri(b, m, x0, y0, x0, y1, x1, y1, c, c, c);
        tri(b, m, x0, y0, x1, y1, x1, y0, c, c, c);
    }

    private static void radial(BufferBuilder b, Matrix4f m, float cx, float cy, float r0, float r1, int c0, int c1, int segs, float a, float bOff) {
        for (int i = 0; i < segs; i++) {
            float t0 = MathHelper.TAU * i / segs, t1 = MathHelper.TAU * (i + 1) / segs;
            float ax = cx + MathHelper.cos(t0) * r0, ay = cy + MathHelper.sin(t0) * r0;
            float bx = cx + MathHelper.cos(t1) * r0, by = cy + MathHelper.sin(t1) * r0;
            float cxo = cx + MathHelper.cos(t0) * r1, cyo = cy + MathHelper.sin(t0) * r1;
            float dx = cx + MathHelper.cos(t1) * r1, dy = cy + MathHelper.sin(t1) * r1;
            tri(b, m, ax, ay, cxo, cyo, dx, dy, c0, c1, c1);
            tri(b, m, ax, ay, dx, dy, bx, by, c0, c1, c0);
        }
    }
}
