package com.animecore.item;

import com.animecore.api.AnimeModContext;
import com.animecore.clan.ClanAbilityManager;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.AnimeRegistries;
import com.animecore.technique.TechniqueTokens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * Changes what the user is: assigns {@code lineage} (Stone Mask -> vampire, Ripple training -> Hamon user, a cybernetic
 * overhaul...). Sneak + use to confirm when the user already has another lineage (it replaces it). Consumed unless
 * {@code reusable}.
 */
public class LineageAwakeningItem extends Item {
    private final AnimeModContext context;
    private final Identifier lineage;
    private final String loreKey;
    private final boolean reusable;

    public LineageAwakeningItem(AnimeModContext context, Identifier lineage, String loreKey, boolean reusable, Settings settings) {
        super(settings.maxCount(reusable ? 1 : 16));
        this.context = context;
        this.lineage = lineage;
        this.loreKey = loreKey;
        this.reusable = reusable;
    }

    public Identifier lineage() { return lineage; }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!(user instanceof ServerPlayerEntity player)) return TypedActionResult.success(stack, true);
        PlayerPowerComponent power = context.power(player);
        var def = AnimeRegistries.lineage(player.getRegistryManager(), lineage).orElse(null);
        if (def == null) return TypedActionResult.fail(stack);
        if (power.lineage().map(lineage::equals).orElse(false)) {
            player.sendMessage(Text.translatable("message.animecore.awaken.known", def.name()).formatted(Formatting.GRAY), true);
            return TypedActionResult.fail(stack);
        }
        if (power.lineage().isPresent() && !player.isSneaking()) {
            player.sendMessage(Text.translatable("message.animecore.lineage.confirm", def.name()).formatted(Formatting.YELLOW), true);
            return TypedActionResult.fail(stack);
        }
        if (!ClanAbilityManager.assign(player, power, lineage, ClanAbilityManager.Reason.SEALED_SCROLL)) return TypedActionResult.fail(stack);
        power.setClanInitialized(true);
        TechniqueTokens.ensureTokens(player, power);
        if (!reusable && !player.isCreative()) stack.decrement(1);
        player.getServerWorld().spawnParticles(ParticleTypes.FLASH, player.getX(), player.getBodyY(0.6), player.getZ(), 1, 0, 0, 0, 0);
        player.getServerWorld().spawnParticles(ParticleTypes.ENCHANT, player.getX(), player.getBodyY(0.5), player.getZ(), 60, 0.5, 1, 0.5, 0.5);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 0.4f, 1.6f);
        return TypedActionResult.success(stack, false);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext ctx, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable(loreKey).formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("tooltip.animecore.lineage_awaken").formatted(Formatting.GOLD));
    }
}
