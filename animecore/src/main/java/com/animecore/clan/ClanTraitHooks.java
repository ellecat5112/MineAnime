package com.animecore.clan;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.LineageDefinition;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

import java.util.List;

/**
 * Wires {@link IClanTrait} hooks into the engine event bus and converts kill experience into clan skill experience.
 * Only the traits of the player's <i>current</i> lineage run, so a clan switch removes their behaviour at once.
 */
public final class ClanTraitHooks {
    private ClanTraitHooks() {}

    public static void register() {
        AnimeCoreEvents.ENERGY_CONSUME.register((player, power, pool, amount) -> {
            float value = amount;
            for (IClanTrait t : traits(player, power)) value = t.modifyEnergyCost(player, power, pool, value);
            return value;
        });
        AnimeCoreEvents.REACTIVE_DEFENSE.register((player, source, amount) -> {
            for (AnimeModContext ctx : AnimeModRegistry.all()) {
                PlayerPowerComponent power = ctx.power(player);
                for (IClanTrait t : traits(player, power)) if (t.tryEvade(player, power, source, amount)) return true;
            }
            return false;
        });
        AnimeCoreEvents.FATAL_DAMAGE_INTERCEPT.register((player, source, amount) -> {
            for (AnimeModContext ctx : AnimeModRegistry.all()) {
                PlayerPowerComponent power = ctx.power(player);
                for (IClanTrait t : traits(player, power)) if (t.interceptDeath(player, power, source)) return true;
            }
            return false;
        });
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, base, taken, blocked) -> {
            if (blocked || taken <= 0f || !(source.getAttacker() instanceof ServerPlayerEntity player) || entity == player) return;
            for (AnimeModContext ctx : AnimeModRegistry.all()) {
                PlayerPowerComponent power = ctx.power(player);
                for (IClanTrait t : traits(player, power)) t.onDealtDamage(player, power, entity, source, taken);
            }
        });
    }

    /** Once per second from the component tick. */
    public static void tick(ServerPlayerEntity player, PlayerPowerComponent power) {
        for (IClanTrait t : traits(player, power)) t.tick(player, power);
    }

    public static List<IClanTrait> traits(ServerPlayerEntity player, PlayerPowerComponent power) {
        return power.lineage().flatMap(id -> AnimeRegistries.lineage(player.getRegistryManager(), id))
                .map(LineageDefinition::clanTraits).orElse(List.of());
    }

    /**
     * Called from the {@code LivingEntity#dropXp} mixin. If the killer holds (either hand) the skill item of their
     * current clan, the experience the victim would drop becomes clan skill experience instead of orbs.
     *
     * @return true if converted (vanilla orbs must be suppressed)
     */
    public static boolean convertKillExperience(LivingEntity victim, Entity attacker) {
        if (!(attacker instanceof ServerPlayerEntity player) || !(victim.getWorld() instanceof ServerWorld world)) return false;
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            PlayerPowerComponent power = ctx.power(player);
            if (power.lineage().isEmpty()) continue;
            boolean holding = false;
            for (Hand hand : Hand.values()) holding |= ClanRollService.isSkillItemFor(player.getStackInHand(hand), power);
            if (!holding) continue;
            int xp = victim.getXpToDrop(world, player);
            if (xp <= 0) return false;
            ClanAbilityManager.addSkillExperience(player, power, xp);
            return true;
        }
        return false;
    }
}
