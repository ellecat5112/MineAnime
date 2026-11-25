package com.animecore.trait;

import com.mojang.serialization.MapCodec;

public record TraitModifierType<T extends ITraitModifier>(MapCodec<T> codec) {
}
