package com.animecore.registry.definition;

import com.animecore.trait.ITraitModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Multi-stage transformation (Sharingan 1-Tomoe -> 3-Tomoe -> Mangekyo, SSJ1 -> SSJ2, Shikai -> Bankai).
 * {@code data/<ns>/animecore/transformation/<id>.json}
 *
 * @param durationTicks   0 = lasts until toggled off or the pool runs dry
 * @param nextStage       evolves into this form once {@code masteryToEvolve} ticks have been spent in this form
 */
public record TransformationDefinition(
        String system,
        Text name,
        Identifier pool,
        float activationCost,
        float drainPerTick,
        int durationTicks,
        int cooldownTicks,
        int requiredTier,
        List<ITraitModifier> modifiers,
        Optional<Identifier> nextStage,
        int masteryToEvolve,
        Optional<Identifier> requiredLineage,
        List<Identifier> grantedAbilities,
        Visuals visuals,
        Optional<Identifier> token,
        List<com.animecore.clan.IClanTrait> traits
) {
    /** Without bloodline-style traits. */
    public TransformationDefinition(String system, Text name, Identifier pool, float activationCost, float drainPerTick, int durationTicks,
                                    int cooldownTicks, int requiredTier, List<ITraitModifier> modifiers, Optional<Identifier> nextStage,
                                    int masteryToEvolve, Optional<Identifier> requiredLineage, List<Identifier> grantedAbilities, Visuals visuals,
                                    Optional<Identifier> token) {
        this(system, name, pool, activationCost, drainPerTick, durationTicks, cooldownTicks, requiredTier, modifiers, nextStage,
                masteryToEvolve, requiredLineage, grantedAbilities, visuals, token, List.of());
    }

    /** Pre-token constructor (forms listed on the system's default forms token). */
    public TransformationDefinition(String system, Text name, Identifier pool, float activationCost, float drainPerTick, int durationTicks,
                                    int cooldownTicks, int requiredTier, List<ITraitModifier> modifiers, Optional<Identifier> nextStage,
                                    int masteryToEvolve, Optional<Identifier> requiredLineage, List<Identifier> grantedAbilities, Visuals visuals) {
        this(system, name, pool, activationCost, drainPerTick, durationTicks, cooldownTicks, requiredTier, modifiers, nextStage,
                masteryToEvolve, requiredLineage, grantedAbilities, visuals, Optional.empty(), List.of());
    }

    /**
     * @param stanceAnimation PlayerAnimator id looped on the BASE layer while active
     * @param auraColor       RGB tint for the procedural aura (no textures)
     * @param geoAttachment   optional GeckoLib attachment model id (Susanoo ribs, tails, wings)
     * @param cloakColor      optional RGB of a translucent shroud drawn over the body (chakra cloaks, curse-mark skin, Kāma)
     * @param cloakAlpha      shroud opacity 0..1
     */
    public record Visuals(Optional<Identifier> stanceAnimation, int auraColor, float auraIntensity, Optional<Identifier> geoAttachment,
                          Optional<Integer> cloakColor, float cloakAlpha) {
        public Visuals(Optional<Identifier> stanceAnimation, int auraColor, float auraIntensity, Optional<Identifier> geoAttachment) {
            this(stanceAnimation, auraColor, auraIntensity, geoAttachment, Optional.empty(), 0.45f);
        }
        public static final Visuals NONE = new Visuals(Optional.empty(), 0xFFFFFF, 0f, Optional.empty());
        public static final Codec<Visuals> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.optionalFieldOf("stance_animation").forGetter(Visuals::stanceAnimation),
                Codec.INT.optionalFieldOf("aura_color", 0xFFFFFF).forGetter(Visuals::auraColor),
                Codec.floatRange(0f, 4f).optionalFieldOf("aura_intensity", 1f).forGetter(Visuals::auraIntensity),
                Identifier.CODEC.optionalFieldOf("geo_attachment").forGetter(Visuals::geoAttachment),
                Codec.INT.optionalFieldOf("cloak_color").forGetter(Visuals::cloakColor),
                Codec.floatRange(0f, 1f).optionalFieldOf("cloak_alpha", 0.45f).forGetter(Visuals::cloakAlpha)
        ).apply(i, Visuals::new));
    }

    public static final Codec<TransformationDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(TransformationDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(TransformationDefinition::name),
            Identifier.CODEC.fieldOf("pool").forGetter(TransformationDefinition::pool),
            Codec.floatRange(0f, Float.MAX_VALUE).optionalFieldOf("activation_cost", 0f).forGetter(TransformationDefinition::activationCost),
            Codec.floatRange(0f, Float.MAX_VALUE).optionalFieldOf("drain_per_tick", 0f).forGetter(TransformationDefinition::drainPerTick),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("duration_ticks", 0).forGetter(TransformationDefinition::durationTicks),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cooldown_ticks", 100).forGetter(TransformationDefinition::cooldownTicks),
            Codec.intRange(0, 1000).optionalFieldOf("required_tier", 1).forGetter(TransformationDefinition::requiredTier),
            ITraitModifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(TransformationDefinition::modifiers),
            Identifier.CODEC.optionalFieldOf("next_stage").forGetter(TransformationDefinition::nextStage),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("mastery_to_evolve", 0).forGetter(TransformationDefinition::masteryToEvolve),
            Identifier.CODEC.optionalFieldOf("required_lineage").forGetter(TransformationDefinition::requiredLineage),
            Identifier.CODEC.listOf().optionalFieldOf("granted_abilities", List.of()).forGetter(TransformationDefinition::grantedAbilities),
            Visuals.CODEC.optionalFieldOf("visuals", Visuals.NONE).forGetter(TransformationDefinition::visuals),
            Identifier.CODEC.optionalFieldOf("token").forGetter(TransformationDefinition::token),
            com.animecore.clan.IClanTrait.CODEC.listOf().optionalFieldOf("traits", List.of()).forGetter(TransformationDefinition::traits)
    ).apply(i, TransformationDefinition::new));
}
