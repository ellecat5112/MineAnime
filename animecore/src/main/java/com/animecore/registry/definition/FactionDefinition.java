package com.animecore.registry.definition;

import com.animecore.trait.ITraitModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Faction / origin (Hidden Leaf, Soul Society, Capsule Corp, Tokyo Jujutsu High, Straw Hat Pirates...).
 * {@code data/<ns>/animecore/faction/<id>.json}
 */
public record FactionDefinition(
        String system,
        Text name,
        int color,
        List<ITraitModifier> traits,
        List<Identifier> startingAbilities,
        List<Identifier> allies,
        List<Identifier> rivals,
        int startingReputation,
        boolean selectable
) {
    public static final Codec<FactionDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(FactionDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(FactionDefinition::name),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(FactionDefinition::color),
            ITraitModifier.CODEC.listOf().optionalFieldOf("traits", List.of()).forGetter(FactionDefinition::traits),
            Identifier.CODEC.listOf().optionalFieldOf("starting_abilities", List.of()).forGetter(FactionDefinition::startingAbilities),
            Identifier.CODEC.listOf().optionalFieldOf("allies", List.of()).forGetter(FactionDefinition::allies),
            Identifier.CODEC.listOf().optionalFieldOf("rivals", List.of()).forGetter(FactionDefinition::rivals),
            Codec.INT.optionalFieldOf("starting_reputation", 0).forGetter(FactionDefinition::startingReputation),
            Codec.BOOL.optionalFieldOf("selectable", true).forGetter(FactionDefinition::selectable)
    ).apply(i, FactionDefinition::new));
}
