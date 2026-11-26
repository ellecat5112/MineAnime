package com.narutorpg.datagen;

import com.animecore.loot.AnimeLootHelper;
import com.animecore.loot.SetAbilityScrollFunction;
import com.animecore.loot.SetLineageFunction;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.NarutoContent;
import com.narutorpg.content.NarutoItems;
import com.narutorpg.content.NarutoTools;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/** Chest loot built with animecore's Lootr-safe helpers. */
final class NarutoLootTableProvider extends SimpleFabricLootTableProvider {
    NarutoLootTableProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries, LootContextTypes.CHEST);
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> exporter) {
        String system = NarutoRPG.MOD_ID;
        exporter.accept(NarutoItems.SHINOBI_CACHE, LootTable.builder()
                .pool(AnimeLootHelper.pool(1, 2)
                        .with(AnimeLootHelper.randomScroll(NarutoItems.JUTSU_SCROLL, system, 3, 6))
                        .with(ItemEntry.builder(NarutoItems.JUTSU_SCROLL).weight(2)
                                .apply(SetAbilityScrollFunction.fixed(NarutoContent.SUBSTITUTION_JUTSU.getValue())))
                        .with(EmptyEntry.builder().weight(4)))
                .pool(AnimeLootHelper.pool(1, 1)
                        .with(AnimeLootHelper.elementalWeapon(NarutoItems.KUNAI, system, 10))
                        .with(AnimeLootHelper.elementalWeapon(NarutoItems.CHAKRA_BLADE, system, 1))
                        .with(ItemEntry.builder(NarutoItems.LEAF_HEADBAND).weight(3))
                        // Bloodlines: an unsealed reroll scroll, a scroll sealed with a (rare) clan, a random clan emblem.
                        .with(ItemEntry.builder(NarutoItems.BLOODLINE_SCROLL).weight(2))
                        .with(ItemEntry.builder(NarutoItems.BLOODLINE_SCROLL).weight(1).apply(SetLineageFunction.random(system, true, 3)))
                        .with(ItemEntry.builder(NarutoItems.CLAN_EMBLEM).weight(2).apply(SetLineageFunction.random(system, false, 3)))
                        .with(ItemEntry.builder(NarutoItems.CLAN_GARB).weight(2).apply(SetLineageFunction.random(system, false, 3)))
                        .with(EmptyEntry.builder().weight(6)))
                // Everyday shinobi supplies from the wiki tool catalog.
                .pool(AnimeLootHelper.pool(1, 3)
                        .with(supply("shuriken", 6, 2, 8)).with(supply("senbon", 4, 3, 9)).with(supply("explosive_tag", 4, 1, 3))
                        .with(supply("smoke_bomb", 3, 1, 3)).with(supply("makibishi", 3, 2, 6)).with(supply("barrier_tag", 3, 1, 4))
                        .with(supply("military_rations_pill", 3, 1, 3)).with(supply("blood_increasing_pill", 2, 1, 2))
                        .with(supply("flash_bomb", 2, 1, 2)).with(supply("wire_strings", 2, 1, 3))));
    }

    private static net.minecraft.loot.entry.LeafEntry.Builder<?> supply(String id, int weight, int min, int max) {
        return ItemEntry.builder(NarutoTools.item(id)).weight(weight)
                .apply(net.minecraft.loot.function.SetCountLootFunction.builder(
                        net.minecraft.loot.provider.number.UniformLootNumberProvider.create(min, max)));
    }
}
