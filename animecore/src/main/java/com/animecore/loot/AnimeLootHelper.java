package com.animecore.loot;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.Set;

/**
 * Loot table plumbing for anime mods.
 * <p>
 * <b>Lootr compatibility</b>: Lootr instances any container that carries a loot table key and rolls it per player.
 * Everything here is a plain loot table / loot function, so injected and custom chest tables become per-player
 * automatically; randomness always comes from the {@code LootContext}.
 */
public final class AnimeLootHelper {
    private AnimeLootHelper() {}

    public static RegistryKey<LootTable> table(Identifier id) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, id);
    }

    public static RegistryKey<LootTable> vanilla(String path) {
        return table(Identifier.ofVanilla(path));
    }

    /**
     * Adds one roll of {@code table} (with {@code chance}) to built-in (non-datapack-overridden) vanilla tables.
     * Modpack datapacks that replace a vanilla table are respected.
     */
    public static void injectInto(Set<RegistryKey<LootTable>> targets, RegistryKey<LootTable> table, float chance) {
        LootTableEvents.MODIFY.register((key, builder, source, registries) -> {
            if (!source.isBuiltin() || !targets.contains(key)) return;
            builder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .conditionally(RandomChanceLootCondition.builder(chance))
                    .with(LootTableEntry.builder(table)));
        });
    }

    // ---------------------------------------------------------------- DataGen builders

    /** A scroll entry that rolls a random ability of {@code system} up to {@code maxTier}. */
    public static LeafEntry.Builder<?> randomScroll(Item scroll, String system, int maxTier, int weight) {
        return ItemEntry.builder(scroll).weight(weight).apply(SetAbilityScrollFunction.random(system, maxTier));
    }

    /** A weapon entry with a random base affinity of {@code system}. */
    public static LeafEntry.Builder<?> elementalWeapon(Item weapon, String system, int weight) {
        return ItemEntry.builder(weapon).weight(weight).apply(SetAnimeWeaponDataFunction.randomAffinity(system));
    }

    /** Standard "cache" pool: {@code min..max} rolls. */
    public static LootPool.Builder pool(int min, int max) {
        return LootPool.builder().rolls(UniformLootNumberProvider.create(min, max));
    }
}
