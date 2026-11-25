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
 * Activation, upkeep, revert and the stage handler (mastery evolution, in-place ascend/descend, fall back one stage
 * when energy runs out). Continuous aura visuals are drawn client-side by {@code ProceduralAuraRenderer} from the
 * synced public state, so no per-tick particle packets are sent.
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
        boolean stageSwap = power.activeTransformation().isPresent();
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
        def.visuals().stanceAnimation().ifPresentOrElse(
                anim -> AnimationService.play(player, AnimLayer.BASE, anim, AnimPlayback.STANCE),
                () -> { if (stageSwap) AnimationService.stop(player, AnimLayer.BASE, 6); });
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

        if (def.durationTicks() > 0 && power.transformationTicks() >= def.durationTicks()) {
            revert(player, power, false);
            player.sendMessage(Text.translatable("message.animecore.transformation_ended", def.name()).formatted(Formatting.GRAY), true);
            return;
        }
        if (!power.tryConsume(def.pool(), def.drainPerTick())) {
            if (!fallBack(player, power, active)) {
                revert(player, power, false);
                player.sendMessage(Text.translatable("message.animecore.transformation_ended", def.name()).formatted(Formatting.GRAY), true);
            }
            return;
        }

        int mastery = power.addMastery(active, 1);
        if (def.nextStage().isPresent() && def.masteryToEvolve() > 0 && mastery == def.masteryToEvolve()
                && power.unlockTransformation(def.nextStage().get())) {
            AnimeRegistries.transformation(player.getRegistryManager(), def.nextStage().get()).ifPresent(next ->
                    player.sendMessage(Text.translatable("message.animecore.evolved", next.value().name()).formatted(Formatting.LIGHT_PURPLE), false));
            player.getServerWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.8f, 1.4f);
        }
    }

    // ------------------------------------------------------------------ stage handler

    /** In-place evolution to the next unlocked stage (SSJ1 -> SSJ2, 3-Tomoe -> Mangekyo). */
    public static boolean ascend(ServerPlayerEntity player, PlayerPowerComponent power) {
        Identifier active = power.activeTransformation().orElse(null);
        if (active == null) return toggle(player, power, null);
        Optional<Identifier> next = AnimeRegistries.transformation(player.getRegistryManager(), active)
                .flatMap(e -> e.value().nextStage());
        if (next.isEmpty() || !power.unlockedTransformations().contains(next.get())) {
            player.sendMessage(Text.translatable("message.animecore.no_higher_stage").formatted(Formatting.RED), true);
            return false;
        }
        return activate(player, power, next.get());
    }

    /** In-place regression to the previous stage; reverts entirely from the base stage. */
    public static boolean descend(ServerPlayerEntity player, PlayerPowerComponent power) {
        Identifier active = power.activeTransformation().orElse(null);
        if (active == null) return false;
        Optional<Identifier> previous = previousStage(player, active);
        if (previous.isPresent() && power.unlockedTransformations().contains(previous.get())) {
            swapStage(player, power, previous.get());
            return true;
        }
        return revert(player, power, true);
    }

    /** Out of energy: drop one stage if the cheaper form can still be sustained this tick. */
    private static boolean fallBack(ServerPlayerEntity player, PlayerPowerComponent power, Identifier active) {
        Optional<Identifier> previous = previousStage(player, active);
        if (previous.isEmpty() || !power.unlockedTransformations().contains(previous.get())) return false;
        Optional<RegistryEntry.Reference<TransformationDefinition>> prev = AnimeRegistries.transformation(player.getRegistryManager(), previous.get());
        if (prev.isEmpty() || !power.tryConsume(prev.get().value().pool(), prev.get().value().drainPerTick())) return false;
        swapStage(player, power, previous.get());
        player.sendMessage(Text.translatable("message.animecore.stage_dropped", prev.get().value().name()).formatted(Formatting.YELLOW), true);
        return true;
    }

    private static void swapStage(ServerPlayerEntity player, PlayerPowerComponent power, Identifier form) {
        power.setActiveTransformation(form);
        AnimeRegistries.transformation(player.getRegistryManager(), form).ifPresent(e ->
                e.value().visuals().stanceAnimation().ifPresentOrElse(
                        anim -> AnimationService.play(player, AnimLayer.BASE, anim, AnimPlayback.STANCE),
                        () -> AnimationService.stop(player, AnimLayer.BASE, 6)));
    }

    /** Reverse lookup of {@code next_stage}. */
    public static Optional<Identifier> previousStage(ServerPlayerEntity player, Identifier form) {
        var registry = player.getRegistryManager().get(AnimeRegistries.TRANSFORMATION);
        for (var entry : registry.getEntrySet()) {
            if (entry.getValue().nextStage().filter(form::equals).isPresent()) return Optional.of(entry.getKey().getValue());
        }
        return Optional.empty();
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
