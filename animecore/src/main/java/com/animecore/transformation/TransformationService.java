package com.animecore.transformation;

import com.animecore.animation.AnimLayer;
import com.animecore.animation.AnimPlayback;
import com.animecore.animation.AnimationService;
import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.platform.AnimeParticles;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.TransformationDefinition;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Activation, upkeep, revert and stage evolution of transformations.
 * (Phase 2 adds the client {@code ProceduralAuraRenderer} and GeckoLib attachments; the server particles here are
 * the always-available fallback.)
 */
public final class TransformationService {
    private TransformationService() {}

    /** @param formId null = toggle: revert if transformed, else activate the most advanced unlocked form */
    public static boolean toggle(ServerPlayerEntity player, PlayerPowerComponent power, @Nullable Identifier formId) {
        Optional<Identifier> active = power.activeTransformation();
        if (formId == null) {
            if (active.isPresent()) return revert(player, power, true);
            formId = bestUnlocked(player, power);
            if (formId == null) {
                player.sendMessage(Text.translatable("message.animecore.no_transformation").formatted(Formatting.RED), true);
                return false;
            }
        }
        if (formId.equals(active.orElse(null))) return revert(player, power, true);
        return activate(player, power, formId);
    }

    public static boolean activate(ServerPlayerEntity player, PlayerPowerComponent power, Identifier formId) {
        Optional<RegistryEntry.Reference<TransformationDefinition>> entry = AnimeRegistries.transformation(player.getRegistryManager(), formId);
        if (entry.isEmpty()) return false;
        TransformationDefinition def = entry.get().value();
        String deny = null;
        if (!def.system().equals(power.context().modId())) deny = "message.animecore.cast.unknown_system";
        else if (!power.unlockedTransformations().contains(formId)) deny = "message.animecore.cast.locked";
        else if (power.tier() < def.requiredTier()) deny = "message.animecore.cast.tier_too_low";
        else if (def.requiredLineage().isPresent() && !def.requiredLineage().equals(power.lineage())) deny = "message.animecore.cast.locked";
        else if (power.isOnCooldown(formId)) deny = "message.animecore.cast.on_cooldown";
        else if (power.pool(def.pool()).map(p -> p.current() < def.activationCost()).orElse(true)) deny = "message.animecore.cast.no_energy";
        if (deny != null) {
            player.sendMessage(Text.translatable(deny).formatted(Formatting.RED), true);
            return false;
        }
        if (!AnimeCoreEvents.TRANSFORMATION_TOGGLE.invoker().allow(player, entry.get(), true)) return false;
        if (!power.tryConsume(def.pool(), def.activationCost())) return false;

        power.activeTransformation().ifPresent(previous -> power.setCooldown(previous, 0)); // stage swap: no penalty
        power.setActiveTransformation(formId);
        def.visuals().stanceAnimation().ifPresent(anim -> AnimationService.play(player, AnimLayer.BASE, anim, AnimPlayback.STANCE));
        burst(player, def);
        player.sendMessage(Text.translatable("message.animecore.transformed", def.name()).formatted(Formatting.GOLD), true);
        return true;
    }

    public static boolean revert(ServerPlayerEntity player, PlayerPowerComponent power, boolean voluntary) {
        Identifier active = power.activeTransformation().orElse(null);
        if (active == null) return false;
        Optional<RegistryEntry.Reference<TransformationDefinition>> entry = AnimeRegistries.transformation(player.getRegistryManager(), active);
        if (voluntary && entry.isPresent() && !AnimeCoreEvents.TRANSFORMATION_TOGGLE.invoker().allow(player, entry.get(), false)) return false;
        power.setActiveTransformation(null);
        entry.ifPresent(e -> power.setCooldown(active, e.value().cooldownTicks()));
        AnimationService.stop(player, AnimLayer.BASE, 8);
        return true;
    }

    /** Upkeep: drain, duration, mastery/evolution, aura particles. Called from the component's server tick. */
    public static void tick(ServerPlayerEntity player, PlayerPowerComponent power) {
        Identifier active = power.activeTransformation().orElse(null);
        if (active == null) return;
        Optional<RegistryEntry.Reference<TransformationDefinition>> entry = AnimeRegistries.transformation(player.getRegistryManager(), active);
        if (entry.isEmpty()) {
            power.setActiveTransformation(null);
            return;
        }
        TransformationDefinition def = entry.get().value();
        power.tickTransformation();

        if (!power.tryConsume(def.pool(), def.drainPerTick())
                || (def.durationTicks() > 0 && power.transformationTicks() >= def.durationTicks())) {
            revert(player, power, false);
            player.sendMessage(Text.translatable("message.animecore.transformation_ended", def.name()).formatted(Formatting.GRAY), true);
            return;
        }

        int mastery = power.addMastery(active, 1);
        if (def.nextStage().isPresent() && def.masteryToEvolve() > 0 && mastery == def.masteryToEvolve()
                && power.unlockTransformation(def.nextStage().get())) {
            AnimeRegistries.transformation(player.getRegistryManager(), def.nextStage().get()).ifPresent(next ->
                    player.sendMessage(Text.translatable("message.animecore.evolved", next.value().name()).formatted(Formatting.LIGHT_PURPLE), false));
            player.getServerWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.8f, 1.4f);
        }

        if (def.visuals().auraIntensity() > 0f && player.age % 4 == 0) {
            int count = Math.max(1, (int) (3 * def.visuals().auraIntensity()));
            player.getServerWorld().spawnParticles(AnimeParticles.dust(def.visuals().auraColor(), 1.1f),
                    player.getX(), player.getY() + 1.0, player.getZ(), count, 0.35, 0.6, 0.35, 0.01);
        }
    }

    /** Most advanced unlocked form: one that is not the {@code previous stage} of another unlocked form. */
    private static @Nullable Identifier bestUnlocked(ServerPlayerEntity player, PlayerPowerComponent power) {
        List<Identifier> unlocked = new ArrayList<>(power.unlockedTransformations());
        Identifier best = null;
        for (Identifier id : unlocked) {
            Optional<RegistryEntry.Reference<TransformationDefinition>> e = AnimeRegistries.transformation(player.getRegistryManager(), id);
            if (e.isEmpty() || power.tier() < e.get().value().requiredTier()) continue;
            boolean superseded = e.get().value().nextStage().map(unlocked::contains).orElse(false);
            if (!superseded) best = id;
        }
        return best;
    }

    private static void burst(ServerPlayerEntity player, TransformationDefinition def) {
        var world = player.getServerWorld();
        world.spawnParticles(AnimeParticles.dust(def.visuals().auraColor(), 2f), player.getX(), player.getY() + 1, player.getZ(),
                40, 0.6, 1.0, 0.6, 0.05);
        world.spawnParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1, player.getZ(), 1, 0, 0, 0, 0);
        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_BEACON_POWER_SELECT, SoundCategory.PLAYERS, 0.7f, 1.6f);
    }
}
