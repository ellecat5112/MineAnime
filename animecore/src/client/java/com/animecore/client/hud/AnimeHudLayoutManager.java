package com.animecore.client.hud;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.client.hud.element.AbilityHotbarElement;
import com.animecore.client.hud.element.ComboTrailElement;
import com.animecore.client.hud.element.EnergyBarsElement;
import com.animecore.client.hud.element.TransformationElement;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Unified HUD for every animecore mod. Each power system gets a group of panels per corner; groups are stacked
 * in registration order, so NarutoRPG + BleachRPG installed together produce two neat stacks instead of overlap.
 * Every system automatically receives the default panels; mods can {@link #remove} or {@link #register} more.
 */
public final class AnimeHudLayoutManager {
    private static final Map<Identifier, AnimeHudElement> ELEMENTS = new LinkedHashMap<>();
    private static final Set<String> DEFAULTS_CREATED = new HashSet<>();
    private static final Set<Identifier> REMOVED = new HashSet<>();

    private AnimeHudLayoutManager() {}

    public static void init() {
        HudRenderCallback.EVENT.register(AnimeHudLayoutManager::render);
    }

    public static void register(AnimeHudElement element) {
        ELEMENTS.put(element.id(), element);
    }

    /** Remove (or suppress a not-yet-created default) element, e.g. {@code narutorpg:hud/combo}. */
    public static void remove(Identifier id) {
        ELEMENTS.remove(id);
        REMOVED.add(id);
    }

    private static void ensureDefaults() {
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            if (!DEFAULTS_CREATED.add(ctx.modId())) continue;
            for (AnimeHudElement e : List.of(new EnergyBarsElement(ctx), new TransformationElement(ctx),
                    new AbilityHotbarElement(ctx), new ComboTrailElement(ctx))) {
                if (!REMOVED.contains(e.id())) ELEMENTS.putIfAbsent(e.id(), e);
            }
        }
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden || client.getDebugHud().shouldShowDebugHud()) return;
        ensureDefaults();
        float tickDelta = tickCounter.getTickDelta(false);
        int sw = context.getScaledWindowWidth();
        int sh = context.getScaledWindowHeight();
        int margin = HudLayout.margin(sw);
        int width = HudLayout.panelWidth(sw);

        // TOP_LEFT: stack downward
        int y = margin;
        for (List<AnimeHudElement> group : groups(HudAnchor.TOP_LEFT, client)) {
            for (AnimeHudElement e : group) {
                e.render(context, client, margin, y, width, tickDelta);
                y += e.height(client, width) + HudLayout.ELEMENT_GAP;
            }
            y += HudLayout.GROUP_GAP;
        }

        // BOTTOM_LEFT: stack upward (chat would cover it while typing)
        if (client.currentScreen instanceof ChatScreen) return;
        int bottom = sh - margin;
        for (List<AnimeHudElement> group : groups(HudAnchor.BOTTOM_LEFT, client)) {
            for (AnimeHudElement e : group) {
                int h = e.height(client, width);
                bottom -= h;
                e.render(context, client, margin, bottom, width, tickDelta);
                bottom -= HudLayout.ELEMENT_GAP;
            }
            bottom -= HudLayout.GROUP_GAP;
        }
    }

    /** Visible elements of one anchor, grouped per power system in registration order. */
    private static List<List<AnimeHudElement>> groups(HudAnchor anchor, MinecraftClient client) {
        List<List<AnimeHudElement>> out = new ArrayList<>();
        boolean several = AnimeModRegistry.all().size() > 1;
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            // with several anime mods installed, only show the ones this player has started using
            if (several && client.player != null && !ctx.maybePower(client.player).map(p -> p.engaged()).orElse(false)) continue;
            List<AnimeHudElement> group = new ArrayList<>();
            for (AnimeHudElement e : ELEMENTS.values()) {
                if (e.context() == ctx && e.anchor() == anchor && e.isVisible(client)) group.add(e);
            }
            if (group.isEmpty()) continue;
            group.sort(Comparator.comparingInt(AnimeHudElement::priority));
            out.add(group);
        }
        return out;
    }
}
