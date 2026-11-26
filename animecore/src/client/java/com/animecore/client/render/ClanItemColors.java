package com.animecore.client.render;

import com.animecore.clan.ClanRollService;
import com.animecore.item.ClanEmblemItem;
import com.animecore.item.ClanGarbItem;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.LineageDefinition;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.ColorHelper;

/**
 * Tints clan items per clan without per-clan textures: emblem layer 1 and garb layer 0 take the clan colour
 * (garb: its dye component, emblem: the synced lineage registry). Layer 0 of the emblem is the untinted frame.
 */
public final class ClanItemColors {
    private ClanItemColors() {}

    public static void register() {
        for (Item item : Registries.ITEM) {
            if (item instanceof ClanEmblemItem) {
                ColorProviderRegistry.ITEM.register((stack, layer) -> layer == 1 ? lineageColor(stack) : -1, item);
            } else if (item instanceof ClanGarbItem) {
                ColorProviderRegistry.ITEM.register((stack, layer) ->
                        layer == 0 ? ColorHelper.Argb.fullAlpha(DyedColorComponent.getColor(stack, 0xD8D8D8)) : -1, item);
            }
        }
    }

    private static int lineageColor(ItemStack stack) {
        var world = MinecraftClient.getInstance().world;
        int rgb = ClanRollService.binding(stack)
                .flatMap(id -> world == null ? java.util.Optional.empty() : AnimeRegistries.lineage(world.getRegistryManager(), id))
                .map(LineageDefinition::color).orElse(0x9E9E9E);
        return ColorHelper.Argb.fullAlpha(rgb);
    }
}
