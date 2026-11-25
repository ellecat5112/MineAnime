package com.animecore.trait;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Collects the combined effect of every active {@link ITraitModifier} for one power system. */
public final class TraitAccumulator {
    public record AttributeBonus(RegistryEntry<EntityAttribute> attribute, double amount, EntityAttributeModifier.Operation operation) {}

    private final List<AttributeBonus> attributes = new ArrayList<>();
    private final Map<Identifier, Float> capacityFlat = new HashMap<>();
    private final Map<Identifier, Float> capacityMult = new HashMap<>();
    private final Map<Identifier, Float> regenMult = new HashMap<>();
    private final Map<Identifier, Float> affinityPower = new HashMap<>();
    private float globalPower = 1f;

    public void addAttribute(RegistryEntry<EntityAttribute> attribute, double amount, EntityAttributeModifier.Operation op) {
        attributes.add(new AttributeBonus(attribute, amount, op));
    }

    public void addCapacity(Identifier pool, float flat, float multiplier) {
        capacityFlat.merge(pool, flat, Float::sum);
        capacityMult.merge(pool, multiplier, (a, b) -> a * b);
    }

    public void multiplyRegen(Identifier pool, float multiplier) {
        regenMult.merge(pool, multiplier, (a, b) -> a * b);
    }

    public void multiplyAbilityPower(@Nullable Identifier affinity, float multiplier) {
        if (affinity == null) globalPower *= multiplier;
        else affinityPower.merge(affinity, multiplier, (a, b) -> a * b);
    }

    public List<AttributeBonus> attributes() {
        return Collections.unmodifiableList(attributes);
    }

    public float capacityFlat(Identifier pool) {
        return capacityFlat.getOrDefault(pool, 0f);
    }

    public float capacityMultiplier(Identifier pool) {
        return capacityMult.getOrDefault(pool, 1f);
    }

    public float regenMultiplier(Identifier pool) {
        return regenMult.getOrDefault(pool, 1f);
    }

    /** Ability damage multiplier for an (optional) affinity. */
    public float abilityPower(@Nullable Identifier affinity) {
        float m = globalPower;
        if (affinity != null) m *= affinityPower.getOrDefault(affinity, 1f);
        return m;
    }
}
