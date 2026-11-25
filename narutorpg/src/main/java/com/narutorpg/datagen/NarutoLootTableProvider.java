package com.narutorpg.datagen;

import com.animecore.loot.AnimeLootHelper;
import com.animecore.loot.SetAbilityScrollFunction;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.NarutoContent;
import com.narutorpg.content.NarutoItems;
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
                        .with(EmptyEntry.builder().weight(6))));
    }
}
