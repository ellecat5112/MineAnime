package com.animecore.registry.definition;

import com.animecore.clan.IClanTrait;
import com.animecore.trait.ITraitModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Bloodline / race / clan (Uchiha, Saiyan, Quincy, Zenin...).
 * {@code data/<ns>/animecore/lineage/<id>.json}
 *
 * <pre>{@code
 * {
 *   "system": "narutorpg",
 *   "name": {"translate": "lineage.narutorpg.uchiha", "fallback": "Uchiha Clan"},
 *   "weight": 4,                                    // roll weight (0 = never rolled)
 *   "selectable": true,                             // may be picked with /animecore clan choose
 *   "innate_affinities": ["narutorpg:fire"],        // starting nature affinities
 *   "traits": [ {"type": "animecore:attribute", ...} ],          // stat modifiers (removed on switch)
 *   "granted_abilities": ["narutorpg:fireball_jutsu"],           // unlocked by default
 *   "progression": [ {"skill_level": 3, "transformation": "narutorpg:sharingan_one_tomoe"},
 *                    {"skill_level": 5, "ability": "narutorpg:fire_release_great_fire_annihilation", "required_tier": 1} ],
 *   "clan_traits": [ {"type": "animecore:energy_discount", ...} ],  // behaviour hooks (events / ticks)
 *   "clan_items": ["narutorpg:gunbai"],             // clan weapons / treasures, given once as a starter kit
 *   "max_skill_level": 50
 * }
 * }</pre>
 *
 * @param traits        passive {@link ITraitModifier}s (attribute modifiers, pool capacity, ability power)
 * @param clanTraits    behavioural {@link IClanTrait}s hooked into events and the server tick
 * @param progression   abilities / forms unlocked by clan skill level (and optionally power tier)
 * @param fallback      the lineage given when nothing else applies (e.g. "Clanless"); never rolled
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
        int weight,
        int color,
        List<ProgressionEntry> progression,
        List<IClanTrait> clanTraits,
        List<Identifier> clanItems,
        int maxSkillLevel,
        boolean fallback
) {
    public static final int DEFAULT_MAX_SKILL_LEVEL = 50;

    /** Legacy (pre-clan) constructor: no progression, traits only. */
    public LineageDefinition(String system, Text name, Text description, List<Identifier> innateAffinities,
                             List<ITraitModifier> traits, List<Identifier> grantedAbilities,
                             List<Identifier> grantedTransformations, boolean selectable, int weight) {
        this(system, name, description, innateAffinities, traits, grantedAbilities, grantedTransformations, selectable,
                weight, 0xFFFFFF, List.of(), List.of(), List.of(), DEFAULT_MAX_SKILL_LEVEL, false);
    }

    /**
     * One progression step. Exactly one of {@code ability} / {@code transformation} is normally present.
     *
     * @param skillLevel    clan skill level at which it unlocks
     * @param requiredTier  minimum power tier as well (rank gate), 1 = none
     */
    public record ProgressionEntry(Optional<Identifier> ability, Optional<Identifier> transformation, int skillLevel, int requiredTier) {
        public static final Codec<ProgressionEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.optionalFieldOf("ability").forGetter(ProgressionEntry::ability),
                Identifier.CODEC.optionalFieldOf("transformation").forGetter(ProgressionEntry::transformation),
                Codec.intRange(1, 1000).fieldOf("skill_level").forGetter(ProgressionEntry::skillLevel),
                Codec.intRange(1, 1000).optionalFieldOf("required_tier", 1).forGetter(ProgressionEntry::requiredTier)
        ).apply(i, ProgressionEntry::new));

        public static ProgressionEntry ability(Identifier id, int level) {
            return new ProgressionEntry(Optional.of(id), Optional.empty(), level, 1);
        }

        public static ProgressionEntry transformation(Identifier id, int level, int tier) {
            return new ProgressionEntry(Optional.empty(), Optional.of(id), level, tier);
        }
    }

    public static final Codec<LineageDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(LineageDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(LineageDefinition::name),
            TextCodecs.CODEC.optionalFieldOf("description", Text.empty()).forGetter(LineageDefinition::description),
            Identifier.CODEC.listOf().optionalFieldOf("innate_affinities", List.of()).forGetter(LineageDefinition::innateAffinities),
            ITraitModifier.CODEC.listOf().optionalFieldOf("traits", List.of()).forGetter(LineageDefinition::traits),
            Identifier.CODEC.listOf().optionalFieldOf("granted_abilities", List.of()).forGetter(LineageDefinition::grantedAbilities),
            Identifier.CODEC.listOf().optionalFieldOf("granted_transformations", List.of()).forGetter(LineageDefinition::grantedTransformations),
            Codec.BOOL.optionalFieldOf("selectable", true).forGetter(LineageDefinition::selectable),
            Codec.intRange(0, 10_000).optionalFieldOf("weight", 10).forGetter(LineageDefinition::weight),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(LineageDefinition::color),
            ProgressionEntry.CODEC.listOf().optionalFieldOf("progression", List.of()).forGetter(LineageDefinition::progression),
            IClanTrait.CODEC.listOf().optionalFieldOf("clan_traits", List.of()).forGetter(LineageDefinition::clanTraits),
            Identifier.CODEC.listOf().optionalFieldOf("clan_items", List.of()).forGetter(LineageDefinition::clanItems),
            Codec.intRange(1, 1000).optionalFieldOf("max_skill_level", DEFAULT_MAX_SKILL_LEVEL).forGetter(LineageDefinition::maxSkillLevel),
            Codec.BOOL.optionalFieldOf("fallback", false).forGetter(LineageDefinition::fallback)
    ).apply(i, LineageDefinition::new));

    /** Can be produced by a weighted roll. */
    public boolean rollable() {
        return weight > 0 && !fallback;
    }
}
