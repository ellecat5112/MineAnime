package com.animecore.item;

import com.animecore.api.AnimeModContext;
import com.animecore.clan.ClanAbilityManager;
import com.animecore.clan.ClanRollService;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.platform.Ids;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

/**
 * Clan reroll. Unsealed: weighted reroll that never returns the current clan (with pity). Sealed (has
 * {@code animecore:clan_binding}, e.g. from the {@code animecore:set_lineage} loot function): awakens exactly that
 * bloodline. Abilities the player learned outside the clan are kept; clan-granted ones are swapped.
 */
public class BloodlineScrollItem extends Item implements AnimeComponentItem {
    private final AnimeModContext context;

    public BloodlineScrollItem(AnimeModContext context, Settings settings) {
        super(settings.maxCount(16));
        this.context = context;
    }

    @Override
    public List<ComponentType<?>> subtypeComponents() {
        return List.of(AnimeDataComponents.CLAN_BINDING);
    }

    @Override
    public Text getName(ItemStack stack) {
        return ClanRollService.binding(stack)
                .<Text>map(id -> Text.translatable("item.animecore.bloodline_scroll.sealed", super.getName(stack),
                        Text.translatable(Ids.translationKey("lineage", id))))
                .orElseGet(() -> super.getName(stack));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return ClanRollService.binding(stack).isPresent();
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext ctx, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable(ClanRollService.binding(stack).isPresent()
                ? "tooltip.animecore.bloodline_scroll.sealed" : "tooltip.animecore.bloodline_scroll.random").formatted(Formatting.GRAY));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!(user instanceof ServerPlayerEntity player)) return TypedActionResult.success(stack, true);
        PlayerPowerComponent power = context.power(player);
        Optional<Identifier> sealed = ClanRollService.binding(stack);
        boolean ok;
        if (sealed.isPresent()) {
            if (sealed.equals(power.lineage())) {
                player.sendMessage(Text.translatable("message.animecore.clan.already").formatted(Formatting.RED), true);
                return TypedActionResult.fail(stack);
            }
            ok = ClanAbilityManager.assign(player, power, sealed.get(), ClanAbilityManager.Reason.SEALED_SCROLL);
        } else {
            ok = ClanRollService.rollAndAssign(player, power, true, ClanAbilityManager.Reason.REROLL_ITEM).isPresent();
        }
        if (!ok) return TypedActionResult.fail(stack);
        stack.decrementUnlessCreative(1, player);
        player.getItemCooldownManager().set(this, 40);
        world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1f, 0.7f);
        return TypedActionResult.consume(stack);
    }
}
