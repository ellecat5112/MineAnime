package com.animecore.api.event;

import com.animecore.ability.AbilityContext;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.TransformationDefinition;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

/**
 * Engine-wide event bus. Every content mod (Naruto, Bleach, Dragon Ball...) attaches mechanics here instead of
 * mixing into engine internals. All events fire on the logical server.
 */
public final class AnimeCoreEvents {
    private AnimeCoreEvents() {}

    /** Before energy is consumed. Return {@link ActionResult#FAIL} to cancel, {@link ActionResult#PASS} to continue. */
    public static final Event<PreAbilityCast> PRE_ABILITY_CAST = EventFactory.createArrayBacked(PreAbilityCast.class,
            listeners -> (player, ability, context) -> {
                for (PreAbilityCast l : listeners) {
                    ActionResult r = l.onPreCast(player, ability, context);
                    if (r != ActionResult.PASS) return r;
                }
                return ActionResult.PASS;
            });

    /** After all actions executed successfully. */
    public static final Event<PostAbilityCast> POST_ABILITY_CAST = EventFactory.createArrayBacked(PostAbilityCast.class,
            listeners -> (player, ability, context) -> {
                for (PostAbilityCast l : listeners) l.onPostCast(player, ability, context);
            });

    /**
     * Modify (chain) the amount of energy about to be consumed from a pool. Return the new amount;
     * return {@code 0} to make the consumption free. Fired for casts, charge drains and transformation drains.
     */
    public static final Event<EnergyConsume> ENERGY_CONSUME = EventFactory.createArrayBacked(EnergyConsume.class,
            listeners -> (player, power, pool, amount) -> {
                float value = amount;
                for (EnergyConsume l : listeners) value = l.modifyCost(player, power, pool, value);
                return Math.max(0f, value);
            });

    /** Return {@code false} to veto a transformation activation ({@code activating=true}) or a voluntary revert. */
    public static final Event<TransformationToggle> TRANSFORMATION_TOGGLE = EventFactory.createArrayBacked(TransformationToggle.class,
            listeners -> (player, form, activating) -> {
                for (TransformationToggle l : listeners) if (!l.allow(player, form, activating)) return false;
                return true;
            });

    /**
     * Fired when a player would die. Return {@code true} to intercept the death (Izanagi, Senzu revival,
     * Hollowfication...). The engine guarantees health is restored to at least 1 when intercepted.
     */
    public static final Event<FatalDamageIntercept> FATAL_DAMAGE_INTERCEPT = EventFactory.createArrayBacked(FatalDamageIntercept.class,
            listeners -> (player, source, amount) -> {
                for (FatalDamageIntercept l : listeners) if (l.intercept(player, source, amount)) return true;
                return false;
            });

    /**
     * Fired before any damage is applied. Return {@code true} to fully negate the hit (Substitution, Z-Vanish,
     * Flash Step). Built-in armed REACTIVE abilities run after listeners pass.
     */
    public static final Event<ReactiveDefense> REACTIVE_DEFENSE = EventFactory.createArrayBacked(ReactiveDefense.class,
            listeners -> (player, source, amount) -> {
                for (ReactiveDefense l : listeners) if (l.tryDefend(player, source, amount)) return true;
                return false;
            });

    @FunctionalInterface
    public interface PreAbilityCast {
        ActionResult onPreCast(ServerPlayerEntity player, RegistryEntry<AbilityDefinition> ability, AbilityContext context);
    }

    @FunctionalInterface
    public interface PostAbilityCast {
        void onPostCast(ServerPlayerEntity player, RegistryEntry<AbilityDefinition> ability, AbilityContext context);
    }

    @FunctionalInterface
    public interface EnergyConsume {
        float modifyCost(ServerPlayerEntity player, PlayerPowerComponent power, Identifier pool, float amount);
    }

    @FunctionalInterface
    public interface TransformationToggle {
        boolean allow(ServerPlayerEntity player, RegistryEntry<TransformationDefinition> form, boolean activating);
    }

    @FunctionalInterface
    public interface FatalDamageIntercept {
        boolean intercept(ServerPlayerEntity player, DamageSource source, float amount);
    }

    @FunctionalInterface
    public interface ReactiveDefense {
        boolean tryDefend(ServerPlayerEntity player, DamageSource source, float amount);
    }
}
