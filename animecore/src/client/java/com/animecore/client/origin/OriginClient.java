package com.animecore.client.origin;

import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.item.AnimeDataComponents;
import com.animecore.item.FactionGarbItem;
import com.animecore.item.FactionHeadbandItem;
import com.animecore.origin.OpenOriginScreenPayload;
import com.animecore.origin.VillageAccessManager;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.FactionDefinition;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;

/** Client wiring for origins: the selection screen, village gear tint / scratched model, and access tooltips. */
public final class OriginClient {
    private OriginClient() {}

    public static void init() {
        AnimeClientNetworking.registerReceiver(OpenOriginScreenPayload.ID, (payload, client) ->
                client.setScreen(new OriginSelectionScreen(payload.system())));

        for (Item item : Registries.ITEM) {
            if (item instanceof FactionHeadbandItem || item instanceof FactionGarbItem) {
                // layer 0 = cloth in the village colour, layer 1 = metal plate / trim (untinted)
                ColorProviderRegistry.ITEM.register((stack, layer) ->
                        layer == 0 ? ColorHelper.Argb.fullAlpha(DyedColorComponent.getColor(stack, 0x37474F)) : -1, item);
            }
            if (item instanceof FactionHeadbandItem) {
                ModelPredicateProviderRegistry.register(item, com.animecore.platform.Ids.core("scratched"), (stack, world, entity, seed) -> {
                    AnimeDataComponents.FactionBadge b = stack.get(AnimeDataComponents.FACTION_BADGE);
                    return b != null && b.scratched() ? 1f : 0f;
                });
            }
        }

        // "Village: Hidden Sand (members or reputation >= 50)" + whether the local player may use it
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return;
            var rm = client.world.getRegistryManager();
            VillageAccessManager.requirement(rm, stack).ifPresent(req -> {
                Text name = AnimeRegistries.faction(rm, req.faction()).map(FactionDefinition::name).orElse(Text.literal(req.faction().toString()));
                lines.add(com.animecore.api.AnimeText.tr(req.faction().getNamespace(), "tooltip.animecore.village.requires", name, req.minReputation()).formatted(Formatting.GOLD));
                boolean ok = VillageAccessManager.access(client.player, stack) == VillageAccessManager.Access.FULL;
                lines.add(Text.translatable(ok ? "tooltip.animecore.village.allowed" : com.animecore.api.AnimeText.key(req.faction().getNamespace(), "tooltip.animecore.village.restricted"))
                        .formatted(ok ? Formatting.GREEN : Formatting.RED));
            });
        });
    }
}
