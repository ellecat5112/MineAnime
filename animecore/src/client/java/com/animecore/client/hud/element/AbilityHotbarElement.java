package com.animecore.client.hud.element;

import com.animecore.api.AnimeModContext;
import com.animecore.client.hud.HudAnchor;
import com.animecore.client.input.AnimeKeybinds;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.AffinityDefinition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * Quick-cast slots: affinity-tinted squares with initials (no textures), cooldown sweep, charge fill and key hints.
 * Optional ability {@code icon} textures (16x16) are a Phase 3 data field; initials keep the jar at zero art cost.
 */
public final class AbilityHotbarElement extends HudElementBase {
    private static final int SLOT = 18;

    public AbilityHotbarElement(AnimeModContext ctx) {
        super(ctx, "hotbar", HudAnchor.BOTTOM_LEFT, 0);
    }

    @Override
    public boolean isVisible(MinecraftClient client) {
        return power(client).map(p -> p.hotbar().stream().anyMatch(Optional::isPresent)).orElse(false);
    }

    private int slotSize(int width) {
        return Math.min(SLOT, (width - (ctx.hotbarSize() - 1) * 2) / ctx.hotbarSize());
    }

    @Override public int height(MinecraftClient client, int width) { return slotSize(width) + 9; }

    @Override
    public void render(DrawContext c, MinecraftClient client, int x, int y, int width, float tickDelta) {
        PlayerPowerComponent power = power(client).orElse(null);
        if (power == null || client.world == null) return;
        int size = slotSize(width);
        var rm = client.world.getRegistryManager();
        for (int slot = 0; slot < ctx.hotbarSize(); slot++) {
            int sx = x + slot * (size + 2);
            Optional<Identifier> id = power.hotbarSlot(slot);
            c.fill(sx, y, sx + size, y + size, PANEL_BG);
            if (id.isPresent()) {
                Optional<AbilityDefinition> def = AnimeRegistries.ability(rm, id.get()).map(RegistryEntry::value);
                int color = def.flatMap(AbilityDefinition::affinity)
                        .flatMap(a -> AnimeRegistries.affinities(rm).getOrEmpty(a)).map(AffinityDefinition::color)
                        .orElseGet(() -> ctx.pools().get(0).color());
                c.fill(sx + 1, y + 1, sx + size - 1, y + size - 1, argb(color, 0x70));
                String initials = def.map(d -> initials(d.name().getString())).orElse("?");
                c.drawCenteredTextWithShadow(client.textRenderer, initials, sx + size / 2, y + (size - 8) / 2, 0xFFFFFFFF);

                if (id.get().equals(power.chargingAbility().orElse(null)) && def.isPresent()) {
                    float charge = power.chargeTicks() / (float) def.get().maxChargeTicks();
                    int h = Math.round((size - 2) * charge);
                    c.fill(sx + 1, y + size - 1 - h, sx + size - 1, y + size - 1, argb(lighten(color), 0x90));
                }
                int remaining = power.cooldownRemaining(id.get());
                if (remaining > 0 && def.isPresent() && def.get().cooldownTicks() > 0) {
                    int h = Math.round(size * Math.min(1f, remaining / (float) def.get().cooldownTicks()));
                    c.fill(sx, y + size - h, sx + size, y + size, 0xB0000000);
                    String secs = String.valueOf((remaining + 19) / 20);
                    c.drawCenteredTextWithShadow(client.textRenderer, secs, sx + size / 2, y + (size - 8) / 2, 0xFFFF6060);
                }
                boolean armed = id.get().equals(power.armedReactive().orElse(null));
                c.drawBorder(sx, y, size, size, armed ? 0xFF66FFFF : argb(color, 0xFF));
            } else {
                c.drawBorder(sx, y, size, size, 0x60FFFFFF);
            }
            AnimeKeybinds.find(ctx, "hotbar_" + (slot + 1)).map(KeyBinding::getBoundKeyLocalizedText).ifPresent(text -> {
                String key = text.getString();
                if (key.length() > 3) key = key.substring(0, 3);
                c.drawCenteredTextWithShadow(client.textRenderer, key, sx + size / 2, y + size + 1, 0xFFB0B0B0);
            });
        }
    }

    /** "Fire Release: Great Fireball Technique" -> "GF"; "Substitution Jutsu" -> "SJ". */
    static String initials(String name) {
        String core = name.contains(":") ? name.substring(name.indexOf(':') + 1) : name;
        StringBuilder sb = new StringBuilder();
        for (String word : core.trim().split("\\s+")) {
            if (!word.isEmpty() && Character.isLetterOrDigit(word.charAt(0))) sb.append(Character.toUpperCase(word.charAt(0)));
            if (sb.length() == 2) break;
        }
        return sb.isEmpty() ? "?" : sb.toString();
    }
}
