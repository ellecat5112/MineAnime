package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.registry.AnimeBuiltinRegistries;
import com.mojang.serialization.Codec;

/**
 * A composable unit of ability behaviour, selected in JSON by {@code "type"}.
 * <pre>{ "type": "animecore:projectile", "speed": 1.4, "damage": 8.0, ... }</pre>
 * Kamehameha, Getsuga Tensho and Fireball Jutsu all reuse these same codecs with different numbers.
 */
public interface IAbilityAction {
    Codec<IAbilityAction> CODEC = AnimeBuiltinRegistries.ABILITY_ACTION_TYPES.getCodec()
            .dispatch("type", IAbilityAction::type, AbilityActionType::codec);

    AbilityActionType<?> type();

    /** Runs on the logical server. */
    void execute(AbilityContext context);
}
