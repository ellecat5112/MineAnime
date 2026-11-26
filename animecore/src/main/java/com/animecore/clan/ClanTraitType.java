package com.animecore.clan;

import com.mojang.serialization.MapCodec;

/** Registry entry for an {@link IClanTrait} codec ({@code animecore:clan_trait_type}). */
public record ClanTraitType<T extends IClanTrait>(MapCodec<T> codec) {}
