package com.animecore.registry.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Elemental / type affinity with a data-driven clash matrix.
 * {@code data/<ns>/animecore/affinity/<id>.json}
 *
 * @param strongAgainst       this affinity deals {@code advantageMultiplier}x to these, and takes 1/x from them
 * @param fusionOf            non-empty = a fused affinity (Kekkei Genkai: Lava = Fire + Earth)
 * @param requiredLineage     fusion only unlocks for this lineage
 */
public record AffinityDefinition(
        String system,
        Text name,
        int color,
        List<Identifier> strongAgainst,
        float advantageMultiplier,
        List<Identifier> fusionOf,
        Optional<Identifier> requiredLineage
) {
    public static final Codec<AffinityDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(AffinityDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(AffinityDefinition::name),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(AffinityDefinition::color),
            Identifier.CODEC.listOf().optionalFieldOf("strong_against", List.of()).forGetter(AffinityDefinition::strongAgainst),
            Codec.floatRange(1f, 10f).optionalFieldOf("advantage_multiplier", 1.5f).forGetter(AffinityDefinition::advantageMultiplier),
            Identifier.CODEC.listOf().optionalFieldOf("fusion_of", List.of()).forGetter(AffinityDefinition::fusionOf),
            Identifier.CODEC.optionalFieldOf("required_lineage").forGetter(AffinityDefinition::requiredLineage)
    ).apply(i, AffinityDefinition::new));
}
