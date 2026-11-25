package com.animecore.client.hud.element;

import com.animecore.api.AnimeModContext;
import com.animecore.client.hud.HudAnchor;
import com.animecore.component.EnergyPool;
import com.animecore.component.PlayerPowerComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/** Chakra / Ki / Reiatsu + stamina bars with values, plus tier. */
public final class EnergyBarsElement extends HudElementBase {
    private static final int ROW = 13;

    public EnergyBarsElement(AnimeModContext ctx) {
        super(ctx, "energy", HudAnchor.TOP_LEFT, 0);
    }

    @Override public boolean isVisible(MinecraftClient client) { return power(client).isPresent(); }

    @Override public int height(MinecraftClient client, int width) { return ctx.pools().size() * ROW; }

    @Override
    public void render(DrawContext c, MinecraftClient client, int x, int y, int width, float tickDelta) {
        PlayerPowerComponent power = power(client).orElse(null);
        if (power == null) return;
        panel(c, x, y, width, height(client, width) - 2);
        int row = y;
        boolean first = true;
        for (EnergyPool pool : power.pools()) {
            Text label = Text.translatable("pool." + pool.id().getNamespace() + "." + pool.id().getPath());
            String value = Math.round(pool.current()) + "/" + Math.round(pool.max());
            c.drawTextWithShadow(client.textRenderer, label, x, row, argb(lighten(pool.spec().color()), 0xFF));
            String right = first ? value + "  T" + power.tier() : value;
            c.drawTextWithShadow(client.textRenderer, right, x + width - client.textRenderer.getWidth(right), row, 0xFFE0E0E0);
            bar(c, x, row + 9, width, 2, pool.ratio(), pool.spec().color());
            row += ROW;
            first = false;
        }
    }
}
