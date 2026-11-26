package com.animecore.ability;

import com.animecore.AnimeCore;
import com.animecore.ability.action.IAbilityAction;
import com.animecore.animation.AnimLayer;
import com.animecore.animation.AnimPlayback;
import com.animecore.animation.AnimationService;
import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.CastType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * Server-side ability pipeline: validation -> PRE event -> energy -> actions -> cooldown/animation/xp -> POST event.
 * Handles quick-cast, sequential combos, hold-to-charge and reactive (dodge/counter) abilities.
 */
public final class AbilityExecutor {
    /** How long a REACTIVE ability stays armed after pressing its key. */
    public static final int REACTIVE_WINDOW_TICKS = 30;

    private AbilityExecutor() {}

    // ---------------------------------------------------------------- input entry points

    /** Key press/release for a specific ability. */
    public static CastResult handleInput(ServerPlayerEntity player, Identifier abilityId, boolean pressed) {
        Optional<RegistryEntry.Reference<AbilityDefinition>> entry = AnimeRegistries.ability(player.getRegistryManager(), abilityId);
        if (entry.isEmpty()) return feedback(player, CastResult.UNKNOWN_ABILITY);
        AbilityDefinition def = entry.get().value();
        Optional<AnimeModContext> ctx = AnimeModRegistry.get(def.system());
        if (ctx.isEmpty()) return feedback(player, CastResult.UNKNOWN_SYSTEM);
        PlayerPowerComponent power = ctx.get().power(player);

        if (!pressed) {
            return abilityId.equals(power.chargingAbility().orElse(null)) ? releaseCharge(player, power) : CastResult.CANCELLED;
        }
        return switch (def.castType()) {
            case INSTANT -> cast(player, entry.get(), power, 0f, null);
            case CHARGED -> beginCharge(player, entry.get(), power);
            case REACTIVE -> arm(player, entry.get(), power);
        };
    }

    /**
     * Sequential combo input (Naruto hand signs). Fires as soon as the buffer exactly matches a castable ability
     * that is not also the prefix of a longer one; otherwise waits for {@code commit}.
     */
    public static CastResult handleComboInput(ServerPlayerEntity player, AnimeModContext ctx, Identifier token, boolean commit) {
        PlayerPowerComponent power = ctx.power(player);
        if (commit && power.comboBuffer().isEmpty()) return CastResult.CANCELLED; // already auto-cast
        if (!commit) {
            power.pushComboToken(token);
            AnimationService.play(player, AnimLayer.UPPER_BODY, ctx.comboAnimation(token), AnimPlayback.UPPER_BODY);
        }
        List<Identifier> buffer = power.comboBuffer();
        RegistryEntry.Reference<AbilityDefinition> exact = null;
        boolean longerExists = false;
        for (Identifier id : power.castableAbilities()) {
            Optional<RegistryEntry.Reference<AbilityDefinition>> e = AnimeRegistries.ability(player.getRegistryManager(), id);
            if (e.isEmpty()) continue;
            List<Identifier> combo = e.get().value().combo();
            if (combo.isEmpty() || !e.get().value().system().equals(ctx.modId())) continue;
            if (combo.equals(buffer)) exact = e.get();
            else if (combo.size() > buffer.size() && combo.subList(0, buffer.size()).equals(buffer)) longerExists = true;
        }
        power.markDirty(false);
        if (exact != null && (commit || !longerExists)) {
            power.clearCombo();
            return exact.value().castType() == CastType.REACTIVE
                    ? arm(player, exact, power)
                    : cast(player, exact, power, exact.value().castType() == CastType.CHARGED ? 0.25f : 0f, null);
        }
        if (commit) {
            power.clearCombo();
            return feedback(player, CastResult.NO_MATCH);
        }
        return CastResult.CHARGING;
    }

    // ---------------------------------------------------------------- core cast

    public static CastResult cast(ServerPlayerEntity player, RegistryEntry<AbilityDefinition> entry, PlayerPowerComponent power,
                                  float chargeRatio, @Nullable Entity reactiveAttacker) {
        CastResult check = checkCastable(player, entry, power);
        if (check != CastResult.SUCCESS) return feedback(player, check);
        AbilityDefinition def = entry.value();
        AbilityContext context = new AbilityContext(player, entry, power, chargeRatio, reactiveAttacker);

        if (AnimeCoreEvents.PRE_ABILITY_CAST.invoker().onPreCast(player, entry, context) == ActionResult.FAIL) {
            return CastResult.CANCELLED;
        }
        if (!power.tryConsume(def.cost().pool(), def.cost().amount())) return feedback(player, CastResult.NO_ENERGY);

        for (IAbilityAction action : def.actions()) {
            try {
                action.execute(context);
            } catch (RuntimeException ex) {
                AnimeCore.LOGGER.error("Ability action {} of {} failed", action.type(), id(entry), ex);
            }
        }

        power.setCooldown(id(entry), def.cooldownTicks());
        AnimationService.play(player, AnimLayer.UPPER_BODY, def.castAnimation()
                .orElseGet(() -> com.animecore.vfx.AbilityVfx.defaultAnimation(def, context.vfxStyle())), AnimPlayback.UPPER_BODY);
        power.addExperience(Math.max(1, Math.round(def.cost().amount() / 5f)));
        AnimeCoreEvents.POST_ABILITY_CAST.invoker().onPostCast(player, entry, context);
        return CastResult.SUCCESS;
    }

    public static CastResult checkCastable(ServerPlayerEntity player, RegistryEntry<AbilityDefinition> entry, PlayerPowerComponent power) {
        AbilityDefinition def = entry.value();
        Identifier id = id(entry);
        if (!def.system().equals(power.context().modId())) return CastResult.UNKNOWN_SYSTEM;
        if (!power.castableAbilities().contains(id)) return CastResult.LOCKED;
        if (power.tier() < def.requiredTier()) return CastResult.TIER_TOO_LOW;
        if (def.requiredTransformation().isPresent()
                && !def.requiredTransformation().get().equals(power.activeTransformation().orElse(null))) return CastResult.WRONG_FORM;
        if (power.isOnCooldown(id)) return CastResult.ON_COOLDOWN;
        return CastResult.SUCCESS;
    }

    // ---------------------------------------------------------------- charge

    private static CastResult beginCharge(ServerPlayerEntity player, RegistryEntry<AbilityDefinition> entry, PlayerPowerComponent power) {
        CastResult check = checkCastable(player, entry, power);
        if (check != CastResult.SUCCESS) return feedback(player, check);
        boolean affordable = power.pool(entry.value().cost().pool()).map(p -> p.current() >= entry.value().cost().amount()).orElse(false);
        if (!affordable) return feedback(player, CastResult.NO_ENERGY);
        power.beginCharge(id(entry));
        power.markDirty(true);
        Identifier holdAnim = entry.value().castAnimation().orElseGet(() -> com.animecore.vfx.AbilityVfx.defaultAnimation(entry.value(),
                com.animecore.vfx.AbilityVfx.resolveStyle(player.getRegistryManager(), entry.value(), id(entry))));
        AnimationService.play(player, AnimLayer.UPPER_BODY, holdAnim, AnimPlayback.UPPER_BODY_HOLD);
        return CastResult.CHARGING;
    }

    /** Called every tick by the component while charging. @return the pool being drained (regen paused). */
    public static @Nullable Identifier tickCharge(ServerPlayerEntity player, PlayerPowerComponent power) {
        Identifier id = power.chargingAbility().orElse(null);
        if (id == null) return null;
        Optional<RegistryEntry.Reference<AbilityDefinition>> entry = AnimeRegistries.ability(player.getRegistryManager(), id);
        if (entry.isEmpty()) {
            power.clearCharge();
            return null;
        }
        AbilityDefinition def = entry.get().value();
        if (power.chargeTicks() < def.maxChargeTicks()) {
            if (!power.tryConsume(def.cost().pool(), def.cost().chargeDrainPerTick())) {
                releaseCharge(player, power);
                return def.cost().pool();
            }
            power.incrementCharge(def.maxChargeTicks());
            power.markDirty(false);
        }
        return def.cost().pool();
    }

    public static CastResult releaseCharge(ServerPlayerEntity player, PlayerPowerComponent power) {
        Identifier id = power.chargingAbility().orElse(null);
        if (id == null) return CastResult.CANCELLED;
        Optional<RegistryEntry.Reference<AbilityDefinition>> entry = AnimeRegistries.ability(player.getRegistryManager(), id);
        int ticks = power.chargeTicks();
        power.clearCharge();
        power.markDirty(true);
        AnimationService.stop(player, AnimLayer.UPPER_BODY, 4);
        if (entry.isEmpty()) return CastResult.UNKNOWN_ABILITY;
        float ratio = ticks / (float) entry.get().value().maxChargeTicks();
        return cast(player, entry.get(), power, ratio, null);
    }

    // ---------------------------------------------------------------- reactive

    private static CastResult arm(ServerPlayerEntity player, RegistryEntry<AbilityDefinition> entry, PlayerPowerComponent power) {
        CastResult check = checkCastable(player, entry, power);
        if (check != CastResult.SUCCESS) return feedback(player, check);
        power.armReactive(id(entry), REACTIVE_WINDOW_TICKS);
        player.sendMessage(Text.translatable("message.animecore.reactive_armed", entry.value().name()).formatted(Formatting.AQUA), true);
        return CastResult.ARMED;
    }

    /** Runs the first armed reactive ability across all power systems. @return true if the hit was negated. */
    public static boolean triggerReactive(ServerPlayerEntity player, DamageSource source) {
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            PlayerPowerComponent power = ctx.power(player);
            Optional<Identifier> armed = power.consumeReactive();
            if (armed.isEmpty()) continue;
            Optional<RegistryEntry.Reference<AbilityDefinition>> entry = AnimeRegistries.ability(player.getRegistryManager(), armed.get());
            if (entry.isPresent() && cast(player, entry.get(), power, 0f, source.getAttacker()) == CastResult.SUCCESS) return true;
        }
        return false;
    }

    // ---------------------------------------------------------------- helpers

    private static Identifier id(RegistryEntry<AbilityDefinition> entry) {
        return entry.getKey().map(k -> k.getValue()).orElseThrow(() -> new IllegalStateException("Unregistered ability entry"));
    }

    private static CastResult feedback(ServerPlayerEntity player, CastResult result) {
        if (!result.ok() && result != CastResult.CANCELLED) {
            player.sendMessage(Text.translatable(result.translationKey()).formatted(Formatting.RED), true);
        }
        return result;
    }
}
