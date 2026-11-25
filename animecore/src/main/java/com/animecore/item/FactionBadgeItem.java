package com.animecore.item;

import com.animecore.api.AnimeModRegistry;
import com.animecore.platform.Ids;
import com.animecore.registry.AnimeRegistries;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/** Headband / badge / emblem. Use it to pledge to its faction (only if you have none in that power system). */
public class FactionBadgeItem extends Item implements AnimeComponentItem {
    public FactionBadgeItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public List<ComponentType<?>> subtypeComponents() {
        return List.of(AnimeDataComponents.FACTION_BADGE);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        AnimeDataComponents.FactionBadge badge = stack.get(AnimeDataComponents.FACTION_BADGE);
        if (badge == null) return TypedActionResult.pass(stack);
        if (!(user instanceof ServerPlayerEntity player)) return TypedActionResult.success(stack, true);
        return AnimeRegistries.faction(player.getRegistryManager(), badge.faction())
                .flatMap(def -> AnimeModRegistry.get(def.system()).map(ctx -> {
                    var power = ctx.power(player);
                    if (power.faction().isPresent()) {
                        player.sendMessage(Text.translatable("message.animecore.badge.pledged").formatted(Formatting.RED), true);
                        return TypedActionResult.fail(stack);
                    }
                    power.setFaction(badge.faction());
                    player.sendMessage(Text.translatable("commands.animecore.faction", player.getDisplayName(), def.name())
                            .formatted(Formatting.GREEN), false);
                    return TypedActionResult.consume(stack);
                }))
                .orElse(TypedActionResult.fail(stack));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        AnimeDataComponents.FactionBadge badge = stack.get(AnimeDataComponents.FACTION_BADGE);
        if (badge != null) tooltip.add(Text.translatable(Ids.translationKey("faction", badge.faction())).formatted(Formatting.GREEN));
    }
}
