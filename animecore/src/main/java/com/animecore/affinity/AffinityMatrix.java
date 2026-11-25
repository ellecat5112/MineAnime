package com.animecore.affinity;

import com.animecore.registry.definition.AffinityDefinition;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Data-driven clash matrix and Kekkei-Genkai-style fusion resolution. Pure functions, side-agnostic. */
public final class AffinityMatrix {
    private AffinityMatrix() {}

    /**
     * Damage multiplier for an attack of {@code attack} affinity against a defender holding {@code defenders}.
     * Advantages multiply, disadvantages divide (Fire vs Wind defender = 1.5x; Water defender = 1/1.5x).
     */
    public static float clash(Registry<AffinityDefinition> registry, Identifier attack, Collection<Identifier> defenders) {
        AffinityDefinition attacker = registry.get(attack);
        if (attacker == null || defenders.isEmpty()) return 1f;
        float multiplier = 1f;
        for (Identifier defenderId : defenders) {
            if (attacker.strongAgainst().contains(defenderId)) multiplier *= attacker.advantageMultiplier();
            AffinityDefinition defender = registry.get(defenderId);
            if (defender != null && defender.strongAgainst().contains(attack)) multiplier /= defender.advantageMultiplier();
        }
        return multiplier;
    }

    /** Every fused affinity whose components are all owned (and whose lineage requirement, if any, matches). */
    public static Set<Identifier> resolveFusions(Registry<AffinityDefinition> registry, Set<Identifier> owned, @Nullable Identifier lineage) {
        Set<Identifier> out = new LinkedHashSet<>();
        for (Map.Entry<RegistryKey<AffinityDefinition>, AffinityDefinition> e : registry.getEntrySet()) {
            AffinityDefinition def = e.getValue();
            if (def.fusionOf().isEmpty() || !owned.containsAll(def.fusionOf())) continue;
            if (def.requiredLineage().isPresent() && !def.requiredLineage().get().equals(lineage)) continue;
            out.add(e.getKey().getValue());
        }
        return out;
    }
}
