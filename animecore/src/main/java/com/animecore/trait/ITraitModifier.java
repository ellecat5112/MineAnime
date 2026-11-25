package com.animecore.trait;

import com.animecore.registry.AnimeBuiltinRegistries;
import com.mojang.serialization.Codec;

/**
 * Polymorphic passive modifier granted by lineages, factions and transformations.
 * <pre>{ "type": "animecore:attribute", "attribute": "minecraft:generic.movement_speed", "amount": 0.1, "operation": "add_multiplied_base" }</pre>
 */
public interface ITraitModifier {
    Codec<ITraitModifier> CODEC = AnimeBuiltinRegistries.TRAIT_MODIFIER_TYPES.getCodec()
            .dispatch("type", ITraitModifier::type, TraitModifierType::codec);

    TraitModifierType<?> type();

    /** Contribute this modifier to the accumulator (pure function: no side effects on the world). */
    void accumulate(TraitAccumulator accumulator);
}
