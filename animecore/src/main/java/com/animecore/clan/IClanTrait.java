package com.animecore.clan;

import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.AnimeBuiltinRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

/**
 * Behavioural clan trait (Aburame insect sense, Hyūga 360° vision, Uzumaki vitality, Hōzuki hydrification...).
 * Unlike {@link com.animecore.trait.ITraitModifier} (pure stat accumulation), a clan trait reacts to engine events.
 * Selected in JSON by {@code "type"}:
 * <pre>{ "type": "animecore:evasion", "chance": 0.1 }</pre>
 * Every hook has a no-op default so a trait only overrides what it needs. Hooks run on the logical server and are
 * only invoked while the player currently belongs to the lineage that declares the trait, so removing a clan
 * removes its behaviour immediately (state-carrying traits clean up in {@link #onRemoved}).
 */
public interface IClanTrait {
    Codec<IClanTrait> CODEC = AnimeBuiltinRegistries.CLAN_TRAIT_TYPES.getCodec()
            .dispatch("type", IClanTrait::type, ClanTraitType::codec);

    ClanTraitType<?> type();

    /** Once per second. */
    default void tick(ServerPlayerEntity player, PlayerPowerComponent power) {}

    /** Chainable energy cost modifier (casts, charge drains, transformation drains). */
    default float modifyEnergyCost(ServerPlayerEntity player, PlayerPowerComponent power, Identifier pool, float amount) { return amount; }

    /** Return true to negate an incoming hit completely. */
    default boolean tryEvade(ServerPlayerEntity player, PlayerPowerComponent power, DamageSource source, float amount) { return false; }

    /** Return true to survive a fatal hit (engine restores at least 1 HP). */
    default boolean interceptDeath(ServerPlayerEntity player, PlayerPowerComponent power, DamageSource source) { return false; }

    /** After the player damaged {@code target} (melee or ability). */
    default void onDealtDamage(ServerPlayerEntity player, PlayerPowerComponent power, LivingEntity target, DamageSource source, float amount) {}

    /** Multiplier applied to clan skill experience. */
    default float skillExperienceMultiplier() { return 1f; }

    default void onAssigned(ServerPlayerEntity player, PlayerPowerComponent power) {}

    default void onRemoved(ServerPlayerEntity player, PlayerPowerComponent power) {}
}
