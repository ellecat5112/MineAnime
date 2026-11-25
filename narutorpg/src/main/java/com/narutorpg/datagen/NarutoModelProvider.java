package com.narutorpg.datagen;

import com.narutorpg.content.NarutoItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

final class NarutoModelProvider extends FabricModelProvider {
    NarutoModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override public void generateBlockStateModels(BlockStateModelGenerator generator) {}

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(NarutoItems.KUNAI, Models.HANDHELD);
        generator.register(NarutoItems.CHAKRA_BLADE, Models.HANDHELD);
        generator.register(NarutoItems.JUTSU_SCROLL, Models.GENERATED);
        generator.register(NarutoItems.LEAF_HEADBAND, Models.GENERATED);
    }
}
