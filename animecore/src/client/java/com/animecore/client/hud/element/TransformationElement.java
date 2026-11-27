package com.animecore.client.hud.element;

import com.animecore.api.AnimeModContext;
import com.animecore.client.hud.HudAnchor;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.movement.MovementMode;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.TransformationDefinition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Active transformation (aura swatch, name, mastery progress) and active movement modes. */
public final class TransformationElement extends HudElementBase {
    public TransformationElement(AnimeModContext ctx) {
        super(ctx, "transformation", HudAnchor.TOP_LEFT, 10);
    }

    @Override
    public boolean isVisible(MinecraftClient client) {
        return power(client).map(p -> p.activeTransformation().isPresent() || !p.activeMovement().isEmpty()).orElse(false);
    }

    @Override
    public int height(MinecraftClient client, int width) {
        return power(client).map(p -> (p.activeTransformation().isPresent() ? 14 : 0) + (p.activeMovement().isEmpty() ? 0 : 10)).orElse(0);
    }

    @Override
    public void render(DrawContext c, MinecraftClient client, int x, int y, int width, float tickDelta) {
        PlayerPowerComponent power = power(client).orElse(null);
        if (power == null || client.world == null) return;
        panel(c, x, y, width, height(client, width) - 2);
        int row = y;
        Optional<TransformationDefinition> form = power.activeTransformation()
                .flatMap(id -> AnimeRegistries.transformation(client.world.getRegistryManager(), id)).map(RegistryEntry::value);
        if (form.isPresent()) {
            TransformationDefinition def = form.get();
            int color = def.visuals().auraColor();
            c.fill(x, row, x + 9, row + 9, argb(color, 0xFF));
            c.drawBorder(x, row, 9, 9, argb(lighten(color), 0xFF));
            c.drawTextWithShadow(client.textRenderer, def.name(), x + 12, row + 1, argb(lighten(color), 0xFF));
            float progress;
            if (def.durationTicks() > 0) progress = 1f - power.transformationTicks() / (float) def.durationTicks();
            else if (def.nextStage().isPresent() && def.masteryToEvolve() > 0)
                progress = power.mastery(power.activeTransformation().get()) / (float) def.masteryToEvolve();
            else progress = 1f;
            animatedBar(c, "form", x + 12, row + 10, width - 12, 2, progress, color, client.world.getTime(), tickDelta);
            row += 14;
        }
        if (!power.activeMovement().isEmpty()) {
            List<String> names = new ArrayList<>();
            for (MovementMode m : power.activeMovement()) names.add(Text.translatable(m.translationKey()).getString());
            c.drawTextWithShadow(client.textRenderer, "» " + String.join(", ", names), x + 1, row, argb(lighten(secondary()), 0xFF));
        }
    }
}
