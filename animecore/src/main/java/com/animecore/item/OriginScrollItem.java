package com.animecore.item;

import com.animecore.api.AnimeModContext;
import com.animecore.origin.OriginSelectionService;
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

/** "Forged papers": clears the origin and reopens the village / background screen (the only non-operator reset). */
public class OriginScrollItem extends Item {
    private final AnimeModContext context;

    public OriginScrollItem(AnimeModContext context, Settings settings) {
        super(settings.maxCount(1));
        this.context = context;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity player) {
            OriginSelectionService.reset(player, context.power(player));
            stack.decrementUnlessCreative(1, player);
            OriginSelectionService.openScreen(player, context);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext ctx, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.animecore.origin_scroll").formatted(Formatting.GRAY));
    }
}
