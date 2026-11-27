package com.animecore.client.origin;

import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.origin.OriginSelectionService;
import com.animecore.origin.SelectOriginPayload;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.BackgroundDefinition;
import com.animecore.registry.definition.FactionDefinition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * First-join origin screen: pick a village and an upbringing, or randomize. A plain vanilla {@link Screen} talking to the
 * server through one {@link SelectOriginPayload}, so it works on dedicated servers and Essential-hosted worlds alike;
 * the server validates everything. Data comes from the synced faction / background registries.
 */
public final class OriginSelectionScreen extends Screen {
    private static final int ROW = 20;

    private final String system;
    private List<Map.Entry<Identifier, FactionDefinition>> villages = List.of();
    private List<Map.Entry<Identifier, BackgroundDefinition>> backgrounds = List.of();
    private Identifier village;
    private Identifier background;
    private int villageScroll, backgroundScroll;

    public OriginSelectionScreen(String system) {
        super(Text.translatable("screen.animecore.origin.title"));
        this.system = system;
    }

    private DynamicRegistryManager rm() {
        MinecraftClient c = MinecraftClient.getInstance();
        return c.world == null ? null : c.world.getRegistryManager();
    }

    @Override
    protected void init() {
        DynamicRegistryManager rm = rm();
        if (rm == null) return;
        villages = OriginSelectionService.factions(rm, system).stream().filter(e -> e.getValue().selectable()).toList();
        backgrounds = OriginSelectionService.backgrounds(rm, system).stream().filter(e -> e.getValue().selectable()).toList();
        if (village == null && !villages.isEmpty()) village = villages.get(0).getKey();
        rebuild();
    }

    private int listTop() { return 44; }
    private int listRows() { return Math.max(3, (height - listTop() - 44) / ROW); }
    private int colW() { return Math.min(150, (width - 40) / 4); }

    private void rebuild() {
        clearChildren();
        int x0 = 12, x1 = x0 + colW() + 8, rows = listRows();
        for (int i = 0; i < rows && i + villageScroll < villages.size(); i++) {
            var e = villages.get(i + villageScroll);
            boolean sel = e.getKey().equals(village);
            MutableText label = e.getValue().name().copy().styled(s -> s.withColor(e.getValue().color()));
            if (sel) label = Text.literal("» ").append(label);
            addDrawableChild(ButtonWidget.builder(label, b -> { village = e.getKey(); if (!allowed(background)) background = null; rebuild(); })
                    .dimensions(x0, listTop() + i * ROW, colW(), ROW - 2).build());
        }
        List<Map.Entry<Identifier, BackgroundDefinition>> bgs = backgrounds.stream().filter(e -> village == null || e.getValue().allows(village)).toList();
        for (int i = 0; i < rows && i + backgroundScroll < bgs.size(); i++) {
            var e = bgs.get(i + backgroundScroll);
            boolean sel = e.getKey().equals(background);
            Text label = sel ? Text.literal("» ").append(e.getValue().name()).formatted(Formatting.YELLOW) : e.getValue().name();
            addDrawableChild(ButtonWidget.builder(label, b -> { background = e.getKey(); rebuild(); })
                    .dimensions(x1, listTop() + i * ROW, colW(), ROW - 2).build());
        }
        int by = height - 32;
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.animecore.origin.random"), b -> {
            AnimeClientNetworking.send(new SelectOriginPayload(system, Optional.empty(), Optional.empty(), true));
            close();
        }).dimensions(width / 2 - 154, by, 150, 20).build());
        ButtonWidget confirm = ButtonWidget.builder(Text.translatable("screen.animecore.origin.confirm"), b -> {
            AnimeClientNetworking.send(new SelectOriginPayload(system, Optional.ofNullable(village), Optional.ofNullable(background), false));
            close();
        }).dimensions(width / 2 + 4, by, 150, 20).build();
        confirm.active = village != null && (background != null || bgs.isEmpty());
        addDrawableChild(confirm);
    }

    private boolean allowed(Identifier bg) {
        if (bg == null) return true;
        return backgrounds.stream().filter(e -> e.getKey().equals(bg)).findFirst().map(e -> e.getValue().allows(village)).orElse(false);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        int x1 = 12 + colW() + 8;
        int step = v > 0 ? -1 : 1;
        if (mx < x1) villageScroll = Math.max(0, Math.min(Math.max(0, villages.size() - listRows()), villageScroll + step));
        else if (mx < x1 + colW()) backgroundScroll = Math.max(0, Math.min(Math.max(0, backgrounds.size() - listRows()), backgroundScroll + step));
        rebuild();
        return true;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, 10, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, com.animecore.api.AnimeText.tr(system, "screen.animecore.origin.subtitle").formatted(Formatting.GRAY), width / 2, 22, 0xAAAAAA);
        int x0 = 12, x1 = x0 + colW() + 8, x2 = x1 + colW() + 12;
        ctx.drawTextWithShadow(textRenderer, com.animecore.api.AnimeText.tr(system, "screen.animecore.origin.villages").formatted(Formatting.GOLD), x0, listTop() - 11, 0xFFFFFF);
        ctx.drawTextWithShadow(textRenderer, Text.translatable("screen.animecore.origin.backgrounds").formatted(Formatting.GOLD), x1, listTop() - 11, 0xFFFFFF);
        DynamicRegistryManager rm = rm();
        if (rm == null) return;
        int w = width - x2 - 12, y = listTop();
        ctx.fill(x2 - 4, y - 4, width - 8, height - 40, 0x88000000);
        if (village != null) {
            FactionDefinition f = AnimeRegistries.faction(rm, village).orElse(null);
            if (f != null) {
                ctx.drawTextWithShadow(textRenderer, f.name().copy().formatted(Formatting.BOLD), x2, y, f.color());
                y += 12;
                ctx.drawTextWithShadow(textRenderer, Text.translatable("screen.animecore.origin.kind." + f.kind()).formatted(Formatting.DARK_GRAY), x2, y, 0x888888);
                y += 12;
                if (!f.description().getString().isEmpty()) {
                    ctx.drawTextWrapped(textRenderer, f.description(), x2, y, w, 0xDDDDDD);
                    y += textRenderer.getWrappedLinesHeight(f.description(), w) + 6;
                }
                y = line(ctx, x2, y, w, com.animecore.api.AnimeText.tr(system, "screen.animecore.origin.jutsu", f.jutsuPool().size(), names(rm, f.jutsuPool(), 4)));
                y = line(ctx, x2, y, w, com.animecore.api.AnimeText.tr(system, "screen.animecore.origin.exclusive", f.exclusive().items().size()));
                y = line(ctx, x2, y, w, Text.translatable("screen.animecore.origin.kit", items(f.kit().weapons(), f.kit().clothing())));
                if (f.rogue()) y = line(ctx, x2, y, w, com.animecore.api.AnimeText.tr(system, "screen.animecore.origin.rogue_warning").formatted(Formatting.RED));
                y += 6;
            }
        }
        if (background != null) {
            BackgroundDefinition b = AnimeRegistries.background(rm, background).orElse(null);
            if (b != null) {
                ctx.drawTextWithShadow(textRenderer, b.name().copy().formatted(Formatting.BOLD, Formatting.YELLOW), x2, y, 0xFFFF55);
                y += 12;
                if (!b.description().getString().isEmpty()) {
                    ctx.drawTextWrapped(textRenderer, b.description(), x2, y, w, 0xDDDDDD);
                    y += textRenderer.getWrappedLinesHeight(b.description(), w) + 6;
                }
                y = line(ctx, x2, y, w, Text.translatable("screen.animecore.origin.bonus", b.startingSkillPoints(), b.startingTierBonus(),
                        items(b.bonusItems(), List.of())));
                if (!b.startingAbilities().isEmpty()) line(ctx, x2, y, w, Text.translatable("screen.animecore.origin.bg_jutsu", names(rm, b.startingAbilities(), 4)));
            }
        } else {
            ctx.drawTextWithShadow(textRenderer, Text.translatable("screen.animecore.origin.pick_background").formatted(Formatting.GRAY), x2, y, 0x999999);
        }
    }

    private int line(DrawContext ctx, int x, int y, int w, Text t) {
        ctx.drawTextWrapped(textRenderer, t, x, y, w, 0xCCCCCC);
        return y + textRenderer.getWrappedLinesHeight(t, w) + 3;
    }

    private static Text names(DynamicRegistryManager rm, List<Identifier> ids, int max) {
        MutableText t = Text.empty();
        for (int i = 0; i < Math.min(max, ids.size()); i++) {
            if (i > 0) t.append(", ");
            t.append(AnimeRegistries.ability(rm, ids.get(i)).map(e -> e.value().name()).orElse(Text.literal(ids.get(i).getPath())));
        }
        if (ids.size() > max) t.append("...");
        return t;
    }

    private static Text items(List<ItemStack> a, List<ItemStack> b) {
        MutableText t = Text.empty();
        int n = 0;
        for (List<ItemStack> list : List.of(a, b)) for (ItemStack s : list) {
            if (n++ > 0) t.append(", ");
            t.append(s.getName());
            if (s.getCount() > 1) t.append(" x" + s.getCount());
        }
        return n == 0 ? Text.literal("-") : t;
    }

    @Override
    public boolean shouldPause() { return false; }
}
