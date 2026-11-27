package com.animecore.client.hud.element;

import com.animecore.api.AnimeModContext;
import com.animecore.client.hud.AnimeHudElement;
import com.animecore.client.hud.HudAnchor;
import com.animecore.component.PlayerPowerComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Shared plumbing and the themed look of the default panels: a dark glass panel with the mod's accent stripe and cut
 * corners ({@link com.animecore.api.AnimeTheme}), animated gradient bars with a "damage ghost" that lingers where the
 * value just dropped from, and a pulse when a bar runs low.
 */
abstract class HudElementBase implements AnimeHudElement {
    protected static final int PANEL_BG = 0x90101018;
    protected final AnimeModContext ctx;
    private final Identifier id;
    private final HudAnchor anchor;
    private final int priority;

    HudElementBase(AnimeModContext ctx, String name, HudAnchor anchor, int priority) {
        this.ctx = ctx;
        this.id = ctx.id("hud/" + name);
        this.anchor = anchor;
        this.priority = priority;
    }

    @Override public Identifier id() { return id; }
    @Override public AnimeModContext context() { return ctx; }
    @Override public HudAnchor anchor() { return anchor; }
    @Override public int priority() { return priority; }

    protected Optional<PlayerPowerComponent> power(MinecraftClient client) {
        return client.player == null ? Optional.empty() : ctx.maybePower(client.player);
    }

    protected static int argb(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    protected int accent() { return ctx.theme().accent(); }
    protected int secondary() { return ctx.theme().secondary(); }

    /** Dark glass panel, accent stripe on the left, accent hairline on top fading out, cut top-right corner. */
    protected void panel(DrawContext c, int x, int y, int w, int h) {
        int x0 = x - 3, y0 = y - 3, x1 = x + w + 3, y1 = y + h + 3;
        c.fillGradient(x0, y0, x1, y1, 0xD8121018, 0xC01A1626);
        c.fill(x0, y0, x0 + 2, y1, argb(accent(), 0xFF));
        int seg = Math.max(1, (x1 - x0) / 12);
        for (int i = 0; i < 12; i++) {
            int a = 0xE0 - i * 0x12;
            c.fill(x0 + i * seg, y0, x0 + (i + 1) * seg, y0 + 1, argb(accent(), Math.max(0, a)));
        }
        c.fill(x0, y1 - 1, x1, y1, argb(accent(), 0x50));
        for (int i = 0; i < 4; i++) c.fill(x1 - 4 + i, y0, x1, y0 + 1 + i, 0);   // notch (cleared corner)
        c.fill(x1 - 5, y0, x1, y0 + 1, 0);
    }

    /** Rank badge: accent slab with the rank in the secondary colour. */
    protected void badge(DrawContext c, MinecraftClient client, int right, int y, String text) {
        int w = client.textRenderer.getWidth(text) + 6;
        int x = right - w;
        c.fill(x, y - 1, right, y + 9, argb(accent(), 0xFF));
        c.fill(x, y - 1, right, y, argb(lighten(accent()), 0xFF));
        c.fill(x - 2, y + 1, x, y + 7, argb(accent(), 0xA0));
        c.drawTextWithShadow(client.textRenderer, text, x + 3, y, argb(secondary(), 0xFF));
    }

    // ---- animated bars ----

    private static final class BarState { float shown = -1, ghost = -1; long ghostHold; }
    private final Map<Object, BarState> bars = new HashMap<>();

    /**
     * Gradient bar easing towards {@code ratio}; a pale "ghost" stays where the value dropped from for half a second,
     * then drains; below 20% the frame pulses.
     */
    protected void animatedBar(DrawContext c, Object key, int x, int y, int w, int h, float ratio, int rgb, long time, float delta) {
        ratio = MathHelper.clamp(ratio, 0f, 1f);
        BarState s = bars.computeIfAbsent(key, k -> new BarState());
        if (s.shown < 0) { s.shown = ratio; s.ghost = ratio; }
        if (ratio < s.shown - 0.01f && s.ghost < s.shown) s.ghost = s.shown;
        if (ratio < s.ghost - 0.01f && s.ghostHold < time) s.ghostHold = time + 10;
        s.shown += (ratio - s.shown) * 0.25f;
        if (time > s.ghostHold) s.ghost += (s.shown - s.ghost) * 0.12f;
        if (s.ghost < s.shown) s.ghost = s.shown;
        c.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xC0000000);
        c.fill(x, y, x + w, y + h, 0xFF1A1A22);
        int ghost = Math.round(w * s.ghost), fill = Math.round(w * s.shown);
        if (ghost > fill) c.fill(x + fill, y, x + ghost, y + h, 0xC0FFF3E0);
        if (fill > 0) {
            c.fillGradient(x, y, x + fill, y + h, argb(lighten(rgb), 0xFF), argb(darken(rgb), 0xFF));
            c.fill(x, y, x + fill, y + 1, argb(0xFFFFFF, 0x70));                  // shine
            int tip = Math.max(x, x + fill - 1);
            c.fill(tip, y, x + fill, y + h, argb(0xFFFFFF, 0xC0));                 // bright leading edge
        }
        for (int t = x + w / 4; t < x + w; t += w / 4) c.fill(t, y + h - 1, t + 1, y + h, 0x40000000);   // quarter ticks
        if (ratio < 0.2f) {
            int a = (int) (0x60 + 0x60 * (0.5f + 0.5f * MathHelper.sin((time + delta) * 0.5f)));
            c.drawBorder(x - 1, y - 1, w + 2, h + 2, argb(0xFF5252, a));
        }
    }

    /** Plain bar (kept for small progress lines). */
    protected static void bar(DrawContext c, int x, int y, int w, int h, float ratio, int rgb) {
        c.fill(x, y, x + w, y + h, 0xFF202028);
        int filled = Math.round(w * Math.max(0f, Math.min(1f, ratio)));
        if (filled > 0) {
            c.fill(x, y, x + filled, y + h, argb(rgb, 0xFF));
            c.fill(x, y, x + filled, y + 1, argb(lighten(rgb), 0xFF));
        }
    }

    protected static int lighten(int rgb) {
        int r = Math.min(255, ((rgb >> 16) & 0xFF) + 60);
        int g = Math.min(255, ((rgb >> 8) & 0xFF) + 60);
        int b = Math.min(255, (rgb & 0xFF) + 60);
        return (r << 16) | (g << 8) | b;
    }

    protected static int darken(int rgb) {
        return (((rgb >> 16) & 0xFF) * 3 / 5 << 16) | (((rgb >> 8) & 0xFF) * 3 / 5 << 8) | ((rgb & 0xFF) * 3 / 5);
    }
}
