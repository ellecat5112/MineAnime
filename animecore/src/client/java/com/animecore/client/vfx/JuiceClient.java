package com.animecore.client.vfx;

import com.animecore.api.AnimeModRegistry;
import com.animecore.api.AnimeTheme;
import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.vfx.Juice;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

/**
 * Client side of {@link Juice}: camera shake (applied in the view bobbing pass, see GameRendererMixin), impact flashes
 * and anime title cards drawn in the mod's theme:
 * <ul>
 *   <li>BOSS - cinematic letterbox, two slashes sweep across the screen, the name slams in huge with its subtitle,
 *       the theme glyph ("ゴゴゴ") pulsing on both sides;</li>
 *   <li>CHAPTER - a lower-third slab with "CHAPTER" and the quest's title;</li>
 *   <li>ARC_END - the frame freezes into sepia and "To Be Continued" slides in on an arrow (JoJo), or an
 *       "Arc Complete" banner.</li>
 * </ul>
 */
public final class JuiceClient {
    private JuiceClient() {}

    private static float shake, shakeMax;
    private static int shakeTicks, shakeTotal;
    private static int flashColor;
    private static float flash;
    private static Juice.CardPayload card;
    private static int cardAge;

    public static void register() {
        AnimeClientNetworking.registerReceiver(Juice.ShakePayload.ID, (p, client) -> {
            if (p.strength() >= shake * (shakeTicks / (float) Math.max(1, shakeTotal))) {
                shakeMax = p.strength();
                shakeTicks = shakeTotal = Math.max(1, p.ticks());
            }
            if (p.flash() > flash) { flash = p.flash(); flashColor = p.flashColor(); }
        });
        AnimeClientNetworking.registerReceiver(Juice.CardPayload.ID, (p, client) -> {
            card = p;
            cardAge = 0;
            if (client.player != null) {
                switch (p.cardKind()) {
                    case BOSS -> client.player.playSound(SoundEvents.ITEM_TRIDENT_THUNDER.value(), 0.6f, 0.7f);
                    case ARC_END -> client.player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 0.8f);
                    case CHAPTER -> client.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 1f, 0.8f);
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (shakeTicks > 0) shakeTicks--;
            shake = shakeTotal == 0 ? 0 : shakeMax * shakeTicks / (float) shakeTotal;
            flash = Math.max(0f, flash - 0.12f);
            if (card != null && ++cardAge > card.ticks()) card = null;
        });
        HudRenderCallback.EVENT.register((ctx, counter) -> render(ctx, counter.getTickDelta(false)));
    }

    /** Called from the view-bobbing pass: a decaying, noisy rotation of the camera. */
    public static void applyShake(MatrixStack m, float tickDelta) {
        if (shake <= 0.001f) return;
        MinecraftClient client = MinecraftClient.getInstance();
        float t = (client.world == null ? 0 : client.world.getTime()) + tickDelta;
        float s = shake * 2.6f;
        m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.sin(t * 2.7f) * s));
        m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(t * 3.3f + 1.3f) * s * 0.8f));
        m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(t * 2.1f + 2.1f) * s * 0.6f));
    }

    private static void render(DrawContext ctx, float delta) {
        MinecraftClient client = MinecraftClient.getInstance();
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();
        if (flash > 0.01f) ctx.fill(0, 0, w, h, ((int) (Math.min(1f, flash) * 200) << 24) | (flashColor == 0 ? 0xFFFFFF : flashColor & 0xFFFFFF));
        if (card == null || client.options.hudHidden) return;
        AnimeTheme theme = AnimeModRegistry.get(card.system()).map(c -> c.theme()).orElse(AnimeTheme.DEFAULT);
        float age = cardAge + delta;
        switch (card.cardKind()) {
            case BOSS -> boss(ctx, client, theme, w, h, age);
            case CHAPTER -> chapter(ctx, client, theme, w, h, age);
            case ARC_END -> arcEnd(ctx, client, theme, w, h, age);
        }
    }

    private static float fade(float age, int total) {
        return MathHelper.clamp(Math.min(age / 4f, (total - age) / 10f), 0f, 1f);
    }

    private static int argb(int rgb, float a) { return ((int) (MathHelper.clamp(a, 0f, 1f) * 255) << 24) | (rgb & 0xFFFFFF); }

    private static void boss(DrawContext ctx, MinecraftClient client, AnimeTheme theme, int w, int h, float age) {
        float f = fade(age, card.ticks());
        int bar = (int) (h * 0.12f * MathHelper.clamp(age / 6f, 0f, 1f) * (f < 1 && age > 10 ? f : 1f));
        ctx.fill(0, 0, w, bar, 0xF0000000);                                       // letterbox
        ctx.fill(0, h - bar, w, h, 0xF0000000);
        float slide = 1f - (float) Math.pow(1f - MathHelper.clamp(age / 7f, 0f, 1f), 3);
        int cy = h / 2;
        MatrixStack m = ctx.getMatrices();
        m.push();                                                                   // two diagonal slashes sweeping in
        m.translate(w / 2f, cy, 0);
        m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-8f));
        int span = (int) (w * 1.4f);
        int x0 = (int) (-span / 2f - span * (1f - slide));
        ctx.fill(x0, -22, x0 + span, 18, argb(theme.accent(), 0.85f * f));
        int x1 = (int) (-span / 2f + span * (1f - slide));
        ctx.fill(x1, 18, x1 + span, 23, argb(theme.secondary(), 0.95f * f));
        ctx.fill(x1, -27, x1 + span, -23, argb(theme.secondary(), 0.95f * f));
        m.pop();
        float scale = 3.2f + 0.8f * (1f - MathHelper.clamp(age / 5f, 0f, 1f));      // the name slams in
        Text title = card.title().copy().formatted(Formatting.BOLD);
        drawCentered(ctx, client, title, w / 2f, cy - 13, scale, argb(theme.secondary(), f), true);
        drawCentered(ctx, client, card.subtitle(), w / 2f, cy + 30, 1.4f, argb(0xFFFFFF, f), true);
        if (theme.glyph() != null) {                                               // ゴゴゴ on both sides
            for (int i = 0; i < 4; i++) {
                float pulse = 1f + 0.15f * MathHelper.sin(age * 0.6f + i);
                float gy = cy - 40 + i * 22 + MathHelper.sin(age * 0.2f + i) * 3;
                drawCentered(ctx, client, Text.literal(theme.glyph()).formatted(Formatting.BOLD), w * 0.12f + i * 6, gy, 2.4f * pulse, argb(theme.accent(), f * 0.95f), true);
                drawCentered(ctx, client, Text.literal(theme.glyph()).formatted(Formatting.BOLD), w * 0.88f - i * 6, gy, 2.4f * pulse, argb(theme.accent(), f * 0.95f), true);
            }
        }
    }

    private static void chapter(DrawContext ctx, MinecraftClient client, AnimeTheme theme, int w, int h, float age) {
        float f = fade(age, card.ticks());
        float slide = 1f - (float) Math.pow(1f - MathHelper.clamp(age / 8f, 0f, 1f), 3);
        int tw = Math.max(client.textRenderer.getWidth(card.title()) * 2, client.textRenderer.getWidth(card.subtitle())) + 30;
        int x = (int) (-tw + (tw + 20) * slide), y = (int) (h * 0.68f);
        ctx.fill(x, y, x + tw, y + 34, argb(0x101018, 0.82f * f));
        ctx.fill(x, y, x + 4, y + 34, argb(theme.accent(), f));
        ctx.fill(x, y + 34, x + tw, y + 36, argb(theme.secondary(), f));
        ctx.drawTextWithShadow(client.textRenderer, Text.translatable("card.animecore.chapter").formatted(Formatting.BOLD), x + 10, y + 4, argb(theme.secondary(), f));
        MatrixStack m = ctx.getMatrices();
        m.push();
        m.translate(x + 10, y + 14, 0);
        m.scale(1.6f, 1.6f, 1f);
        ctx.drawTextWithShadow(client.textRenderer, card.title(), 0, 0, argb(0xFFFFFF, f));
        m.pop();
        ctx.drawTextWithShadow(client.textRenderer, card.subtitle(), x + tw - client.textRenderer.getWidth(card.subtitle()) - 6, y + 4, argb(0xB0B0C0, f));
    }

    private static void arcEnd(DrawContext ctx, MinecraftClient client, AnimeTheme theme, int w, int h, float age) {
        float f = fade(age, card.ticks());
        ctx.fill(0, 0, w, h, argb(0x704214, 0.38f * f));                       // sepia freeze-frame
        ctx.fill(0, 0, w, h, argb(0x000000, 0.15f * f));
        float slide = 1f - (float) Math.pow(1f - MathHelper.clamp((age - 6) / 10f, 0f, 1f), 3);
        Text label = Text.translatable(theme.arcEndKey()).formatted(Formatting.BOLD, Formatting.ITALIC);
        int tw = client.textRenderer.getWidth(label);
        if (theme.arrow()) {                                                      // "To Be Continued" arrow, bottom right
            int ah = 22, bw = (int) (tw * 1.6f) + 30;
            int x = (int) (w + 10 - (bw + 40) * slide), y = (int) (h * 0.62f);   // above the hotbar and dialogue box
            ctx.fill(x + ah, y, x + bw, y + ah, argb(0xE8DCC0, f));
            ctx.fill(x + ah, y + ah - 3, x + bw, y + ah, argb(0x8D6E63, f));
            for (int i = 0; i < ah; i++) {                                         // arrow head pointing left
                int half = ah / 2, d = Math.abs(i - half);
                ctx.fill(x + d, y + i, x + ah, y + i + 1, argb(0xE8DCC0, f));
            }
            MatrixStack m = ctx.getMatrices();
            m.push();
            m.translate(x + ah + 6, y + 4, 0);
            m.scale(1.6f, 1.6f, 1f);
            ctx.drawText(client.textRenderer, label, 0, 0, argb(0x3E2723, f), false);
            m.pop();
        } else {
            int x = w / 2, y = h / 2 - 20;
            int bw = (int) ((tw * 2.4f + 40) * slide);
            ctx.fill(x - bw / 2, y - 6, x + bw / 2, y + 30, argb(0x101018, 0.85f * f));
            ctx.fill(x - bw / 2, y - 6, x + bw / 2, y - 4, argb(theme.accent(), f));
            ctx.fill(x - bw / 2, y + 28, x + bw / 2, y + 30, argb(theme.accent(), f));
            drawCentered(ctx, client, label, x, y, 2.4f, argb(theme.secondary(), f * slide), true);
        }
        drawCentered(ctx, client, card.title(), w / 2f, h * 0.18f, 1.8f, argb(0xFFF3E0, f), true);
    }

    private static void drawCentered(DrawContext ctx, MinecraftClient client, Text text, float cx, float cy, float scale, int color, boolean shadow) {
        MatrixStack m = ctx.getMatrices();
        m.push();
        m.translate(cx, cy, 0);
        m.scale(scale, scale, 1f);
        int tw = client.textRenderer.getWidth(text);
        if (shadow) ctx.drawTextWithShadow(client.textRenderer, text, -tw / 2, -4, color);
        else ctx.drawText(client.textRenderer, text, -tw / 2, -4, color, false);
        m.pop();
    }
}
