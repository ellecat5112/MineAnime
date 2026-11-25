package com.animecore.trait;

import com.animecore.platform.Ids;
import com.animecore.registry.AnimeBuiltinRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Optional;

/** Built-in trait modifier types. */
public final class TraitModifiers {
    private TraitModifiers() {}

    public static final TraitModifierType<AttributeTrait> ATTRIBUTE = register("attribute", AttributeTrait.CODEC);
    public static final TraitModifierType<EnergyCapacityTrait> ENERGY_CAPACITY = register("energy_capacity", EnergyCapacityTrait.CODEC);
    public static final TraitModifierType<EnergyRegenTrait> ENERGY_REGEN = register("energy_regen", EnergyRegenTrait.CODEC);
    public static final TraitModifierType<AbilityPowerTrait> ABILITY_POWER = register("ability_power", AbilityPowerTrait.CODEC);

    private static <T extends ITraitModifier> TraitModifierType<T> register(String path, MapCodec<T> codec) {
        return Registry.register(AnimeBuiltinRegistries.TRAIT_MODIFIER_TYPES, Ids.core(path), new TraitModifierType<>(codec));
    }

    public static void init() {}

    /** Vanilla attribute modifier (speed, attack damage, max health, armor...). */
    public record AttributeTrait(RegistryEntry<EntityAttribute> attribute, double amount,
                                 EntityAttributeModifier.Operation operation) implements ITraitModifier {
        public static final MapCodec<AttributeTrait> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Registries.ATTRIBUTE.getEntryCodec().fieldOf("attribute").forGetter(AttributeTrait::attribute),
                Codec.DOUBLE.fieldOf("amount").forGetter(AttributeTrait::amount),
                EntityAttributeModifier.Operation.CODEC.optionalFieldOf("operation", EntityAttributeModifier.Operation.ADD_VALUE)
                        .forGetter(AttributeTrait::operation)
        ).apply(i, AttributeTrait::new));

        @Override public TraitModifierType<?> type() { return ATTRIBUTE; }
        @Override public void accumulate(TraitAccumulator acc) { acc.addAttribute(attribute, amount, operation); }
    }

    /** Larger energy pool (Uzumaki chakra reserves, Saiyan Ki capacity...). */
    public record EnergyCapacityTrait(Identifier pool, float flat, float multiplier) implements ITraitModifier {
        public static final MapCodec<EnergyCapacityTrait> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Identifier.CODEC.fieldOf("pool").forGetter(EnergyCapacityTrait::pool),
                Codec.FLOAT.optionalFieldOf("flat", 0f).forGetter(EnergyCapacityTrait::flat),
                Codec.FLOAT.optionalFieldOf("multiplier", 1f).forGetter(EnergyCapacityTrait::multiplier)
        ).apply(i, EnergyCapacityTrait::new));

        @Override public TraitModifierType<?> type() { return ENERGY_CAPACITY; }
        @Override public void accumulate(TraitAccumulator acc) { acc.addCapacity(pool, flat, multiplier); }
    }

    public record EnergyRegenTrait(Identifier pool, float multiplier) implements ITraitModifier {
        public static final MapCodec<EnergyRegenTrait> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Identifier.CODEC.fieldOf("pool").forGetter(EnergyRegenTrait::pool),
                Codec.FLOAT.fieldOf("multiplier").forGetter(EnergyRegenTrait::multiplier)
        ).apply(i, EnergyRegenTrait::new));

        @Override public TraitModifierType<?> type() { return ENERGY_REGEN; }
        @Override public void accumulate(TraitAccumulator acc) { acc.multiplyRegen(pool, multiplier); }
    }

    /** Ability damage multiplier, optionally restricted to one affinity (e.g. Uchiha +15% Fire Release). */
    public record AbilityPowerTrait(Optional<Identifier> affinity, float multiplier) implements ITraitModifier {
        public static final MapCodec<AbilityPowerTrait> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
                Identifier.CODEC.optionalFieldOf("affinity").forGetter(AbilityPowerTrait::affinity),
                Codec.FLOAT.fieldOf("multiplier").forGetter(AbilityPowerTrait::multiplier)
        ).apply(i, AbilityPowerTrait::new));

        @Override public TraitModifierType<?> type() { return ABILITY_POWER; }
        @Override public void accumulate(TraitAccumulator acc) { acc.multiplyAbilityPower(affinity.orElse(null), multiplier); }
    }
}
