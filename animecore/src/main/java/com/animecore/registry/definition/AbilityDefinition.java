package com.animecore.registry.definition;

import com.animecore.ability.action.IAbilityAction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * Data-driven ability ({@code data/<ns>/animecore/ability/<id>.json}).
 *
 * @param system                 owning power system (= content mod id), routes to that mod's {@code PlayerPowerComponent}
 * @param combo                  ordered combo tokens (Naruto hand signs, Kido incantation words, DB directional inputs);
 *                               empty = hotbar-only
 * @param requiredTransformation must be in this form to cast (Amaterasu requires Mangekyo)
 * @param castAnimation          PlayerAnimator id played on the UPPER_BODY layer
 */
public record AbilityDefinition(
        String system,
        Text name,
        CastType castType,
        EnergyCost cost,
        int cooldownTicks,
        int requiredTier,
        List<Identifier> combo,
        int maxChargeTicks,
        Optional<Identifier> affinity,
        Optional<Identifier> requiredTransformation,
        Optional<Identifier> castAnimation,
        List<IAbilityAction> actions,
        com.animecore.vfx.AbilityVisuals visuals
) {
    /** Constructor without visuals: the look is derived from the affinity and actions. */
    public AbilityDefinition(String system, Text name, CastType castType, EnergyCost cost, int cooldownTicks, int requiredTier,
                             List<Identifier> combo, int maxChargeTicks, Optional<Identifier> affinity,
                             Optional<Identifier> requiredTransformation, Optional<Identifier> castAnimation, List<IAbilityAction> actions) {
        this(system, name, castType, cost, cooldownTicks, requiredTier, combo, maxChargeTicks, affinity, requiredTransformation,
                castAnimation, actions, com.animecore.vfx.AbilityVisuals.AUTO);
    }

    public static final Codec<AbilityDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(AbilityDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(AbilityDefinition::name),
            CastType.CODEC.optionalFieldOf("cast_type", CastType.INSTANT).forGetter(AbilityDefinition::castType),
            EnergyCost.CODEC.fieldOf("cost").forGetter(AbilityDefinition::cost),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cooldown_ticks", 20).forGetter(AbilityDefinition::cooldownTicks),
            Codec.intRange(0, 1000).optionalFieldOf("required_tier", 1).forGetter(AbilityDefinition::requiredTier),
            Identifier.CODEC.listOf().optionalFieldOf("combo", List.of()).forGetter(AbilityDefinition::combo),
            Codec.intRange(1, 1200).optionalFieldOf("max_charge_ticks", 40).forGetter(AbilityDefinition::maxChargeTicks),
            Identifier.CODEC.optionalFieldOf("affinity").forGetter(AbilityDefinition::affinity),
            Identifier.CODEC.optionalFieldOf("required_transformation").forGetter(AbilityDefinition::requiredTransformation),
            Identifier.CODEC.optionalFieldOf("cast_animation").forGetter(AbilityDefinition::castAnimation),
            IAbilityAction.CODEC.listOf().fieldOf("actions").forGetter(AbilityDefinition::actions),
            com.animecore.vfx.AbilityVisuals.CODEC.optionalFieldOf("visuals", com.animecore.vfx.AbilityVisuals.AUTO).forGetter(AbilityDefinition::visuals)
    ).apply(i, AbilityDefinition::new));
}
