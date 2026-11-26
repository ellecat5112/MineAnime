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
        generator.register(NarutoItems.FORGED_PAPERS, Models.GENERATED);
        // Two-layer clan items: layer0 frame / cloth, layer1 crest / trim (tinted per clan by ClanItemColors).
        twoLayer(generator, NarutoItems.CLAN_EMBLEM);
        twoLayer(generator, NarutoItems.CLAN_GARB);
        twoLayer(generator, NarutoItems.FLAK_JACKET);
        headband(generator);
    }

    /** Forehead protector: cloth (tinted) + plate; the {@code animecore:scratched} predicate swaps in the cut plate. */
    private static void headband(ItemModelGenerator generator) {
        net.minecraft.util.Identifier base = net.minecraft.data.client.ModelIds.getItemModelId(NarutoItems.FOREHEAD_PROTECTOR);
        net.minecraft.util.Identifier scratched = base.withSuffixedPath("_scratched");
        net.minecraft.util.Identifier tex = net.minecraft.data.client.TextureMap.getId(NarutoItems.FOREHEAD_PROTECTOR);
        generator.writer.accept(scratched, () -> layered(tex, tex.withSuffixedPath("_scratched_overlay"), null));
        generator.writer.accept(base, () -> layered(tex, tex.withSuffixedPath("_overlay"), scratched));
    }

    private static com.google.gson.JsonObject layered(net.minecraft.util.Identifier l0, net.minecraft.util.Identifier l1,
                                                      net.minecraft.util.Identifier scratchedModel) {
        com.google.gson.JsonObject o = new com.google.gson.JsonObject();
        o.addProperty("parent", "minecraft:item/generated");
        com.google.gson.JsonObject t = new com.google.gson.JsonObject();
        t.addProperty("layer0", l0.toString());
        t.addProperty("layer1", l1.toString());
        o.add("textures", t);
        if (scratchedModel != null) {
            com.google.gson.JsonArray overrides = new com.google.gson.JsonArray();
            com.google.gson.JsonObject ov = new com.google.gson.JsonObject();
            com.google.gson.JsonObject pred = new com.google.gson.JsonObject();
            pred.addProperty("animecore:scratched", 1);
            ov.add("predicate", pred);
            ov.addProperty("model", scratchedModel.toString());
            overrides.add(ov);
            o.add("overrides", overrides);
        }
        return o;
    }

    private static void twoLayer(ItemModelGenerator generator, net.minecraft.item.Item item) {
        net.minecraft.util.Identifier id = net.minecraft.data.client.ModelIds.getItemModelId(item);
        Models.GENERATED_TWO_LAYERS.upload(id, net.minecraft.data.client.TextureMap.layered(
                net.minecraft.data.client.TextureMap.getId(item), net.minecraft.data.client.TextureMap.getSubId(item, "_overlay")),
                generator.writer);
    }
}
