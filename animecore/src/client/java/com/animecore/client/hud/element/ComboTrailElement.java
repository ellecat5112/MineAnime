package com.animecore.client.hud.element;

import com.animecore.api.AnimeModContext;
import com.animecore.client.hud.HudAnchor;
import com.animecore.component.PlayerPowerComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** Current combo input as chips: "Snake › Ram › Boar". */
public final class ComboTrailElement extends HudElementBase {
    public ComboTrailElement(AnimeModContext ctx) {
        super(ctx, "combo", HudAnchor.BOTTOM_LEFT, 10);
    }

    @Override
    public boolean isVisible(MinecraftClient client) {
        return power(client).map(p -> !p.comboBuffer().isEmpty()).orElse(false);
    }

    @Override public int height(MinecraftClient client, int width) { return 11; }

    @Override
    public void render(DrawContext c, MinecraftClient client, int x, int y, int width, float tickDelta) {
        PlayerPowerComponent power = power(client).orElse(null);
        if (power == null) return;
        int cx = x;
        int color = ctx.pools().get(0).color();
        for (Identifier token : power.comboBuffer()) {
            String label = tokenLabel(token).getString();
            int w = client.textRenderer.getWidth(label) + 6;
            if (cx + w > x + width) break;
            c.fill(cx, y, cx + w, y + 11, argb(color, 0x80));
            c.drawBorder(cx, y, w, 11, argb(lighten(color), 0xFF));
            c.drawTextWithShadow(client.textRenderer, label, cx + 3, y + 2, 0xFFFFFFFF);
            cx += w + 2;
        }
    }

    public static Text tokenLabel(Identifier token) {
        return Text.translatable("combo." + token.getNamespace() + "." + token.getPath());
    }
}
