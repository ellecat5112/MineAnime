package com.animecore.item;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeText;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.manifestation.ManifestationDefinition;
import com.animecore.manifestation.ManifestationService;
import com.animecore.registry.AnimeRegistries;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Awakens a manifestation (Stand). With a {@code target} it grants that one (a Stand Disc); without, it rolls one of the
 * system's manifestations with {@code weight} > 0 (the Stand Arrow - which also wounds whoever it pierces). Sneak + use
 * to replace a manifestation the user already has. Consumed.
 */
public class ManifestationItem extends Item {
    private final AnimeModContext context;
    private final @Nullable Identifier target;
    private final String loreKey;
    private final float pierceDamage;

    public ManifestationItem(AnimeModContext context, @Nullable Identifier target, String loreKey, float pierceDamage, Settings settings) {
        super(settings.maxCount(target == null ? 16 : 1));
        this.context = context;
        this.target = target;
        this.loreKey = loreKey;
        this.pierceDamage = pierceDamage;
    }

    public @Nullable Identifier target() { return target; }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!(user instanceof ServerPlayerEntity player)) return TypedActionResult.success(stack, true);
        PlayerPowerComponent power = context.power(player);
        if (target != null && power.manifestation().map(target::equals).orElse(false)) {
            player.sendMessage(AnimeText.tr(context.modId(), "message.animecore.manifestation.known"), true);
            return TypedActionResult.fail(stack);
        }
        if (power.manifestation().isPresent() && !player.isSneaking()) {
            player.sendMessage(AnimeText.tr(context.modId(), "message.animecore.manifestation.confirm").formatted(Formatting.YELLOW), true);
            return TypedActionResult.fail(stack);
        }
        Identifier id = target != null ? target : roll(player);
        if (id == null || AnimeRegistries.manifestation(player.getRegistryManager(), id).isEmpty()) return TypedActionResult.fail(stack);
        if (pierceDamage > 0f) player.damage(player.getDamageSources().generic(), Math.min(pierceDamage, player.getHealth() - 1f));
        ManifestationService.assign(player, power, id);
        ManifestationService.setManifested(player, power, true);
        player.getServerWorld().spawnParticles(ParticleTypes.END_ROD, player.getX(), player.getBodyY(0.6), player.getZ(), 40, 0.5, 0.9, 0.5, 0.08);
        world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_TRIDENT_RIPTIDE_3.value(), SoundCategory.PLAYERS, 1f, 1.2f);
        if (!player.isCreative()) stack.decrement(1);
        return TypedActionResult.success(stack, false);
    }

    private @Nullable Identifier roll(ServerPlayerEntity player) {
        List<Map.Entry<Identifier, Integer>> pool = new ArrayList<>();
        int total = 0;
        for (var e : player.getRegistryManager().get(AnimeRegistries.MANIFESTATION).getEntrySet()) {
            ManifestationDefinition d = e.getValue();
            if (!d.system().equals(context.modId()) || d.weight() <= 0) continue;
            Identifier id = e.getKey().getValue();
            if (power(player).manifestation().map(id::equals).orElse(false)) continue;
            pool.add(Map.entry(id, d.weight()));
            total += d.weight();
        }
        if (total <= 0) return null;
        int r = player.getRandom().nextInt(total);
        for (var e : pool) if ((r -= e.getValue()) < 0) return e.getKey();
        return pool.get(0).getKey();
    }

    private PlayerPowerComponent power(ServerPlayerEntity p) { return context.power(p); }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext ctx, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable(loreKey).formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(AnimeText.tr(context.modId(), target == null ? "tooltip.animecore.manifestation.random" : "tooltip.animecore.manifestation.grant")
                .formatted(Formatting.LIGHT_PURPLE));
    }
}
