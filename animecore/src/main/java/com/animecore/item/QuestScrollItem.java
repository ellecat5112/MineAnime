package com.animecore.item;

import com.animecore.api.AnimeModContext;
import com.animecore.platform.AnimeNetworking;
import com.animecore.quest.QuestPayloads;
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

/** Story mode journal: use it to open the quest journal of {@code context}'s story (accept, track and abandon quests). */
public class QuestScrollItem extends Item {
    private final AnimeModContext context;

    public QuestScrollItem(AnimeModContext context, Settings settings) {
        super(settings.maxCount(1));
        this.context = context;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity sp) AnimeNetworking.send(sp, new QuestPayloads.OpenJournal(context.modId()));
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext ctx, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.animecore.quest_scroll.tooltip").formatted(Formatting.GRAY));
    }
}
