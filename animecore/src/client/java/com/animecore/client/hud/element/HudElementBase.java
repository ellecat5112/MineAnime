package com.animecore.client.hud.element;

import com.animecore.api.AnimeModContext;
import com.animecore.client.hud.AnimeHudElement;
import com.animecore.client.hud.HudAnchor;
import com.animecore.component.PlayerPowerComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.Optional;

/** Shared plumbing for the default panels. */
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

    protected static void panel(DrawContext c, int x, int y, int w, int h) {
        c.fill(x - 2, y - 2, x + w + 2, y + h + 2, PANEL_BG);
    }

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
}
