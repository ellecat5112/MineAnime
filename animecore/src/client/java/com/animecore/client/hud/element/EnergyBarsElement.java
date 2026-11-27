package com.animecore.client.hud.element;

import com.animecore.api.AnimeModContext;
import com.animecore.client.hud.HudAnchor;
import com.animecore.component.EnergyPool;
import com.animecore.component.PlayerPowerComponent;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/** The mod's header (name + rank badge) and its energy bars: animated, with values and a low-energy pulse. */
public final class EnergyBarsElement extends HudElementBase {
    private static final int HEADER = 12;
    private static final int ROW = 14;
    private final String title;

    public EnergyBarsElement(AnimeModContext ctx) {
        super(ctx, "energy", HudAnchor.TOP_LEFT, 0);
        this.title = FabricLoader.getInstance().getModContainer(ctx.modId()).map(m -> m.getMetadata().getName()).orElse(ctx.modId());
    }

    @Override public boolean isVisible(MinecraftClient client) { return power(client).isPresent(); }

    @Override public int height(MinecraftClient client, int width) { return HEADER + ctx.pools().size() * ROW; }

    @Override
    public void render(DrawContext c, MinecraftClient client, int x, int y, int width, float tickDelta) {
        PlayerPowerComponent power = power(client).orElse(null);
        if (power == null || client.world == null) return;
        panel(c, x, y, width, height(client, width) - 3);
        long time = client.world.getTime();
        c.drawTextWithShadow(client.textRenderer, Text.literal(title.toUpperCase(java.util.Locale.ROOT)).styled(s -> s.withBold(true)),
                x + 1, y, argb(lighten(accent()), 0xFF));
        badge(c, client, x + width, y, Text.translatable("hud.animecore.rank", power.tier()).getString());
        int row = y + HEADER;
        for (EnergyPool pool : power.pools()) {
            Text label = Text.translatable("pool." + pool.id().getNamespace() + "." + pool.id().getPath());
            String value = Math.round(pool.current()) + " / " + Math.round(pool.max());
            c.drawTextWithShadow(client.textRenderer, label, x + 1, row, argb(lighten(pool.spec().color()), 0xFF));
            c.drawTextWithShadow(client.textRenderer, value, x + width - client.textRenderer.getWidth(value), row, 0xFFE8E8F0);
            animatedBar(c, pool.id(), x + 1, row + 9, width - 1, 3, pool.ratio(), pool.spec().color(), time, tickDelta);
            row += ROW;
        }
    }
}
