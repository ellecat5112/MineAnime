package com.animecore.item;

import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Comparator;
import java.util.List;

/** Component-variant stacks for creative tabs and JEI, built from the synced registries (side-agnostic). */
public final class AnimeItemVariants {
    private AnimeItemVariants() {}

    public static List<ItemStack> scrolls(Item scrollItem, String system, RegistryWrapper.WrapperLookup lookup) {
        return lookup.getOptionalWrapper(AnimeRegistries.ABILITY).stream()
                .flatMap(RegistryWrapper::streamEntries)
                .filter(e -> e.value().system().equals(system))
                .sorted(Comparator.comparingInt((RegistryEntry.Reference<AbilityDefinition> e) -> e.value().requiredTier())
                        .thenComparing(e -> e.registryKey().getValue()))
                .map(e -> scroll(scrollItem, e.registryKey().getValue()))
                .toList();
    }

    public static ItemStack scroll(Item scrollItem, Identifier ability) {
        ItemStack stack = new ItemStack(scrollItem);
        stack.set(AnimeDataComponents.ABILITY_SCROLL, new AnimeDataComponents.AbilityScroll(ability));
        return stack;
    }

    /** Base (non-fused) affinities of a system, sorted by id. */
    public static List<Identifier> baseAffinities(String system, RegistryWrapper.WrapperLookup lookup) {
        return lookup.getOptionalWrapper(AnimeRegistries.AFFINITY).stream()
                .flatMap(RegistryWrapper::streamEntries)
                .filter(e -> e.value().system().equals(system) && e.value().fusionOf().isEmpty())
                .map(RegistryEntry.Reference::registryKey).map(RegistryKey::getValue)
                .sorted().toList();
    }

    public static ItemStack infused(Item weapon, Identifier affinity, boolean active) {
        ItemStack stack = new ItemStack(weapon);
        stack.set(AnimeDataComponents.WEAPON_INFUSION, new AnimeDataComponents.WeaponInfusion(java.util.Optional.of(affinity), active));
        return stack;
    }

    public static List<ItemStack> infusedVariants(Item weapon, String system, RegistryWrapper.WrapperLookup lookup) {
        return baseAffinities(system, lookup).stream().map(a -> infused(weapon, a, false)).toList();
    }

    /** One stack per lineage of {@code system} bound with {@code animecore:clan_binding} (dyed for armor). */
    public static List<ItemStack> clanBound(Item item, String system, RegistryWrapper.WrapperLookup lookup) {
        return lookup.getOptionalWrapper(AnimeRegistries.LINEAGE).stream()
                .flatMap(RegistryWrapper::streamEntries)
                .filter(e -> e.value().system().equals(system))
                .sorted(Comparator.comparing(e -> e.registryKey().getValue()))
                .map(e -> com.animecore.clan.ClanRollService.boundStack(item, e.registryKey().getValue(), e.value()))
                .toList();
    }
}
