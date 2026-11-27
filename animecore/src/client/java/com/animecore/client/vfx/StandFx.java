package com.animecore.client.vfx;

import com.animecore.api.AnimeModRegistry;
import com.animecore.api.AnimeTheme;
import com.animecore.entity.ManifestationEntity;
import com.animecore.entity.StoryNpcEntity;
import com.animecore.quest.StoryNpcDefinition;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * World-space flourishes:
 * <ul>
 *   <li>battle glyphs - the mod theme's glyph (JoJo: "ゴ", the menacing sound effect) drifts up and fades around
 *       every manifested Stand and every boss of that mod;</li>
 *   <li>the stopped-time dome - a translucent sphere sweeping out from where time stopped, then a faint shell until
 *       time resumes.</li>
 * </ul>
 */
public final class StandFx {
    private StandFx() {}

    private record Glyph(Vec3d pos, String text, int color, float scale, int born, int life, float drift) {}
    private record Dome(Vec3d center, float radius, long start, long end) {}

    private static final List<Glyph> GLYPHS = new ArrayList<>();
    private static final List<Dome> DOMES = new ArrayList<>();
    private static int clock;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(StandFx::tick);
        WorldRenderEvents.AFTER_TRANSLUCENT.register(StandFx::render);
    }

    public static void dome(Vec3d center, float radius, int ticks) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.world != null) DOMES.add(new Dome(center, radius, c.world.getTime(), c.world.getTime() + ticks));
    }

    private static void tick(MinecraftClient client) {
        clock++;
        GLYPHS.removeIf(g -> clock - g.born > g.life);
        if (client.world == null || client.player == null || client.isPaused()) return;
        DOMES.removeIf(d -> client.world.getTime() > d.end);
        if (clock % 6 != 0) return;
        for (Entity e : client.world.getEntities()) {
            if (e.squaredDistanceTo(client.player) > 48 * 48) continue;
            String system = null;
            float height = 2f;
            if (e instanceof ManifestationEntity m) {
                system = m.definition().map(d -> d.system()).orElse(null);
                height = m.getHeight();
            } else if (e instanceof StoryNpcEntity npc && npc.role() == StoryNpcDefinition.Role.BOSS) {
                system = npc.definition().map(StoryNpcDefinition::system).orElse(null);
                height = npc.getHeight();
            }
            if (system == null) continue;
            AnimeTheme theme = AnimeModRegistry.get(system).map(c -> c.theme()).orElse(AnimeTheme.DEFAULT);
            if (theme.glyph() == null) continue;
            var r = client.world.random;
            double side = r.nextBoolean() ? 1 : -1;
            Vec3d at = e.getPos().add(side * (0.8 + r.nextDouble() * 0.6), height * (0.3 + r.nextDouble() * 0.8), (r.nextDouble() - 0.5) * 1.2);
            GLYPHS.add(new Glyph(at, theme.glyph(), theme.accent(), 0.035f + r.nextFloat() * 0.02f, clock, 26 + r.nextInt(10), (float) side));
        }
        while (GLYPHS.size() > 120) GLYPHS.remove(0);
    }

    private static void render(WorldRenderContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || (GLYPHS.isEmpty() && DOMES.isEmpty())) return;
        Camera cam = ctx.camera();
        Vec3d cp = cam.getPos();
        MatrixStack m = ctx.matrixStack();
        VertexConsumerProvider.Immediate buffers = client.getBufferBuilders().getEntityVertexConsumers();
        float delta = ctx.tickCounter().getTickDelta(false);
        TextRenderer tr = client.textRenderer;
        for (Glyph g : GLYPHS) {
            float age = clock - g.born + delta;
            float t = age / g.life;
            float alpha = MathHelper.clamp(Math.min(t * 5f, (1f - t) * 3f), 0f, 1f);
            Vec3d p = g.pos.add(g.drift * t * 0.25, t * 0.9, 0);
            m.push();
            m.translate(p.x - cp.x, p.y - cp.y, p.z - cp.z);
            m.multiply(cam.getRotation());
            float s = g.scale * (1f + 0.25f * MathHelper.sin(age * 0.5f));
            m.scale(s, -s, s);   // like vanilla name tags (a mirrored scale would be back-face culled)
            Text text = Text.literal(g.text).formatted(Formatting.BOLD);
            int w = tr.getWidth(text);
            Matrix4f mat = m.peek().getPositionMatrix();
            int a = (int) (alpha * 255) << 24;
            tr.draw(text, -w / 2f + 1, 1, a | 0x120018, false, mat, buffers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            tr.draw(text, -w / 2f, 0, a | (g.color & 0xFFFFFF), false, mat, buffers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            m.pop();
        }
        long now = client.world.getTime();
        for (Dome d : DOMES) {
            float age = now - d.start + delta;
            float grow = MathHelper.clamp(age / 12f, 0f, 1f);
            float r = d.radius * (float) (1 - Math.pow(1 - grow, 3));
            float alpha = grow < 1f ? 0.35f : 0.08f * MathHelper.clamp((d.end - now) / 10f, 0f, 1f);
            m.push();
            m.translate(d.center.x - cp.x, d.center.y - cp.y, d.center.z - cp.z);
            sphere(m.peek().getPositionMatrix(), buffers.getBuffer(RenderLayer.getLightning()), r, 0x9575CD, alpha);
            if (grow < 1f) sphere(m.peek().getPositionMatrix(), buffers.getBuffer(RenderLayer.getLightning()), r * 0.92f, 0xE1BEE7, alpha * 0.6f);
            m.pop();
        }
        buffers.draw();
    }

    private static void sphere(Matrix4f mat, VertexConsumer vc, float r, int rgb, float alpha) {
        int rings = 12, segs = 24;
        float cr = ((rgb >> 16) & 255) / 255f, cg = ((rgb >> 8) & 255) / 255f, cb = (rgb & 255) / 255f;
        for (int i = 0; i < rings; i++) {
            float t0 = MathHelper.PI * i / rings, t1 = MathHelper.PI * (i + 1) / rings;
            for (int j = 0; j < segs; j++) {
                float p0 = MathHelper.TAU * j / segs, p1 = MathHelper.TAU * (j + 1) / segs;
                vert(vc, mat, r, t0, p0, cr, cg, cb, alpha);   // both windings: seen from inside and outside
                vert(vc, mat, r, t1, p0, cr, cg, cb, alpha);
                vert(vc, mat, r, t1, p1, cr, cg, cb, alpha);
                vert(vc, mat, r, t0, p1, cr, cg, cb, alpha);
                vert(vc, mat, r, t0, p1, cr, cg, cb, alpha);
                vert(vc, mat, r, t1, p1, cr, cg, cb, alpha);
                vert(vc, mat, r, t1, p0, cr, cg, cb, alpha);
                vert(vc, mat, r, t0, p0, cr, cg, cb, alpha);
            }
        }
    }

    private static void vert(VertexConsumer vc, Matrix4f mat, float r, float theta, float phi, float cr, float cg, float cb, float a) {
        float x = r * MathHelper.sin(theta) * MathHelper.cos(phi), y = r * MathHelper.cos(theta), z = r * MathHelper.sin(theta) * MathHelper.sin(phi);
        vc.vertex(mat, x, y, z).color(cr, cg, cb, a);
    }
}
