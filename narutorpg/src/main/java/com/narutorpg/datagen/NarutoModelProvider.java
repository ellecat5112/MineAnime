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
        generator.register(NarutoItems.BLOODLINE_SCROLL, Models.GENERATED);
        // Two-layer clan items: layer0 frame / cloth, layer1 crest / trim (tinted per clan by ClanItemColors).
        twoLayer(generator, NarutoItems.CLAN_EMBLEM);
        twoLayer(generator, NarutoItems.CLAN_GARB);
    }

    private static void twoLayer(ItemModelGenerator generator, net.minecraft.item.Item item) {
        net.minecraft.util.Identifier id = net.minecraft.data.client.ModelIds.getItemModelId(item);
        Models.GENERATED_TWO_LAYERS.upload(id, net.minecraft.data.client.TextureMap.layered(
                net.minecraft.data.client.TextureMap.getId(item), net.minecraft.data.client.TextureMap.getSubId(item, "_overlay")),
                generator.writer);
    }
}
