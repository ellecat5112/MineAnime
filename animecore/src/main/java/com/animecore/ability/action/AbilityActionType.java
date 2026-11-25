package com.animecore.ability.action;

import com.mojang.serialization.MapCodec;

/** Registry entry describing how to (de)serialize one {@link IAbilityAction} implementation. */
public record AbilityActionType<T extends IAbilityAction>(MapCodec<T> codec) {
}
