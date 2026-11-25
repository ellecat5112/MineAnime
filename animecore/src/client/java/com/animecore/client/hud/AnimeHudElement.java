package com.animecore.client.hud;

import com.animecore.api.AnimeModContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * One stackable HUD panel. The layout manager decides x/y/width; elements only report their height, so any
 * number of anime mods can share a corner without overlapping.
 */
public interface AnimeHudElement {
    /** Namespaced by the owning mod, e.g. {@code narutorpg:hud/energy}. */
    Identifier id();

    AnimeModContext context();

    HudAnchor anchor();

    /** Order inside the owning mod's group (lower = closer to the corner). */
    int priority();

    boolean isVisible(MinecraftClient client);

    /** Height in scaled GUI pixels for the given panel width. */
    int height(MinecraftClient client, int width);

    void render(DrawContext context, MinecraftClient client, int x, int y, int width, float tickDelta);
}
