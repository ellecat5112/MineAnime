package com.animecore.registry.definition;

import com.animecore.trait.ITraitModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Bloodline / race / clan (Uchiha, Saiyan, Quincy, Zenin...).
 * {@code data/<ns>/animecore/lineage/<id>.json}
 */
public record LineageDefinition(
        String system,
        Text name,
        Text description,
        List<Identifier> innateAffinities,
        List<ITraitModifier> traits,
        List<Identifier> grantedAbilities,
        List<Identifier> grantedTransformations,
        boolean selectable,
        int weight
) {
    public static final Codec<LineageDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(LineageDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(LineageDefinition::name),
            TextCodecs.CODEC.optionalFieldOf("description", Text.empty()).forGetter(LineageDefinition::description),
            Identifier.CODEC.listOf().optionalFieldOf("innate_affinities", List.of()).forGetter(LineageDefinition::innateAffinities),
            ITraitModifier.CODEC.listOf().optionalFieldOf("traits", List.of()).forGetter(LineageDefinition::traits),
            Identifier.CODEC.listOf().optionalFieldOf("granted_abilities", List.of()).forGetter(LineageDefinition::grantedAbilities),
            Identifier.CODEC.listOf().optionalFieldOf("granted_transformations", List.of()).forGetter(LineageDefinition::grantedTransformations),
            Codec.BOOL.optionalFieldOf("selectable", true).forGetter(LineageDefinition::selectable),
            Codec.intRange(0, 10_000).optionalFieldOf("weight", 10).forGetter(LineageDefinition::weight)
    ).apply(i, LineageDefinition::new));
}
