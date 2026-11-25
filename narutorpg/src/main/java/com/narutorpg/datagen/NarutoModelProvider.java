package com.narutorpg.datagen;

import com.narutorpg.content.NarutoItems;
import com.narutorpg.content.NarutoTools;
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
        generator.register(NarutoItems.CHAKRA_BLADE, Models.HANDHELD);
        // Ninja tools: held like weapons when they hit hard, otherwise flat items.
        NarutoTools.items().forEach((id, item) -> {
            var spec = NarutoTools.specs().get(id);
            boolean weapon = spec.armor().isEmpty() && spec.attack().damage() >= 3;
            generator.register(item, weapon ? Models.HANDHELD : Models.GENERATED);
        });
        generator.register(NarutoItems.JUTSU_SCROLL, Models.GENERATED);
        generator.register(NarutoItems.LEAF_HEADBAND, Models.GENERATED);
    }
}
