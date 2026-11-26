package com.animecore.registry.definition;

import com.animecore.trait.ITraitModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Upbringing / background archetype chosen with the origin (Academy Prodigy, Shinobi Orphan, Wandering Mercenary...).
 * {@code data/<ns>/animecore/background/<id>.json}
 *
 * @param allowedFactions    factions this background may be combined with (empty = any)
 * @param traits             stat modifiers (attribute, pool capacity / regen, ability power) - applied like lineage traits
 * @param bonusItems         extra starting items
 * @param startingAbilities  unlocked when the origin is confirmed
 * @param startingTierBonus  initial rank modifier (added to the starting tier)
 * @param weight             weight for "randomize origin" (0 = never random)
 */
public record BackgroundDefinition(
        String system,
        Text name,
        Text description,
        List<Identifier> allowedFactions,
        List<ITraitModifier> traits,
        List<ItemStack> bonusItems,
        List<Identifier> startingAbilities,
        int startingSkillPoints,
        int startingTierBonus,
        boolean selectable,
        int weight
) {
    public static final Codec<BackgroundDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(BackgroundDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(BackgroundDefinition::name),
            TextCodecs.CODEC.optionalFieldOf("description", Text.empty()).forGetter(BackgroundDefinition::description),
            Identifier.CODEC.listOf().optionalFieldOf("allowed_factions", List.of()).forGetter(BackgroundDefinition::allowedFactions),
            ITraitModifier.CODEC.listOf().optionalFieldOf("traits", List.of()).forGetter(BackgroundDefinition::traits),
            ItemStack.CODEC.listOf().optionalFieldOf("bonus_items", List.of()).forGetter(BackgroundDefinition::bonusItems),
            Identifier.CODEC.listOf().optionalFieldOf("starting_abilities", List.of()).forGetter(BackgroundDefinition::startingAbilities),
            Codec.intRange(0, 100).optionalFieldOf("starting_skill_points", 0).forGetter(BackgroundDefinition::startingSkillPoints),
            Codec.intRange(0, 100).optionalFieldOf("starting_tier_bonus", 0).forGetter(BackgroundDefinition::startingTierBonus),
            Codec.BOOL.optionalFieldOf("selectable", true).forGetter(BackgroundDefinition::selectable),
            Codec.intRange(0, 10_000).optionalFieldOf("weight", 10).forGetter(BackgroundDefinition::weight)
    ).apply(i, BackgroundDefinition::new));

    public boolean allows(Identifier faction) {
        return allowedFactions.isEmpty() || allowedFactions.contains(faction);
    }
}
