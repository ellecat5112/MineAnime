package com.animecore.item;

import com.animecore.api.AnimeModContext;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.AnimeRegistries;
import com.animecore.technique.TechniqueTokens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
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

/**
 * Grants a transformation (tailed-beast seal, curse mark, sage training scroll, kekkei genkai awakening...): use it to
 * unlock {@code form} - its later stages evolve through mastery - and receive the technique item that lists it.
 * Consumed on use; refused if the form is already known or the user's rank is below the form's requirement.
 */
public class AwakeningItem extends Item {
    private final AnimeModContext context;
    private final Identifier form;
    private final String loreKey;

    public AwakeningItem(AnimeModContext context, Identifier form, String loreKey, Settings settings) {
        super(settings.maxCount(16));
        this.context = context;
        this.form = form;
        this.loreKey = loreKey;
    }

    public Identifier form() { return form; }

    @Override
    public boolean hasGlint(ItemStack stack) { return true; }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!(user instanceof ServerPlayerEntity player)) return TypedActionResult.success(stack, true);
        PlayerPowerComponent power = context.power(player);
        var def = AnimeRegistries.transformation(player.getRegistryManager(), form).orElse(null);
        if (def == null) return TypedActionResult.fail(stack);
        if (power.unlockedTransformations().contains(form)) {
            player.sendMessage(Text.translatable("message.animecore.awaken.known", def.value().name()).formatted(Formatting.GRAY), true);
            return TypedActionResult.fail(stack);
        }
        if (power.tier() < def.value().requiredTier()) {
            player.sendMessage(Text.translatable("message.animecore.awaken.rank", def.value().requiredTier(), power.tier()).formatted(Formatting.RED), true);
            return TypedActionResult.fail(stack);
        }
        power.unlockTransformation(form);
        TechniqueTokens.ensureTokens(player, power);
        if (!player.isCreative()) stack.decrement(1);
        player.sendMessage(Text.translatable("message.animecore.awaken.done", def.value().name()).formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.getServerWorld().spawnParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getBodyY(0.5), player.getZ(), 40, 0.4, 0.8, 0.4, 0.3);
        world.playSound(null, player.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 0.8f, 0.8f);
        return TypedActionResult.success(stack, false);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext ctx, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable(loreKey).formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("tooltip.animecore.awaken").formatted(Formatting.GOLD));
    }
}
