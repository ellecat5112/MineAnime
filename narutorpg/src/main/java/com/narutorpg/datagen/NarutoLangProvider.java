package com.narutorpg.datagen;

import com.narutorpg.content.HandSigns;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;

final class NarutoLangProvider extends FabricLanguageProvider {
    NarutoLangProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, "en_us", registries);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registries, TranslationBuilder t) {
        t.add("affinity.narutorpg.fire", "Fire Release");
        t.add("affinity.narutorpg.wind", "Wind Release");
        t.add("affinity.narutorpg.lightning", "Lightning Release");
        t.add("affinity.narutorpg.earth", "Earth Release");
        t.add("affinity.narutorpg.water", "Water Release");
        t.add("lineage.narutorpg.uchiha", "Uchiha Clan");
        t.add("lineage.narutorpg.uchiha.desc", "Masters of Fire Release, blessed with the Sharingan.");
        t.add("faction.narutorpg.hidden_leaf", "Hidden Leaf Village");
        t.add("ability.narutorpg.fireball_jutsu", "Fire Release: Great Fireball Technique");
        t.add("ability.narutorpg.substitution_jutsu", "Substitution Jutsu");
        t.add("transformation.narutorpg.sharingan_one_tomoe", "Sharingan (1 Tomoe)");
        t.add("transformation.narutorpg.sharingan_two_tomoe", "Sharingan (2 Tomoe)");
        t.add("transformation.narutorpg.sharingan_three_tomoe", "Sharingan (3 Tomoe)");
        t.add("transformation.narutorpg.mangekyo_sharingan", "Mangekyo Sharingan");
        t.add("message.narutorpg.fire_underwater", "Your Fire Release fizzles underwater");

        t.add("key.categories.narutorpg", "NarutoRPG");
        for (int i = 1; i <= 3; i++) t.add("key.narutorpg.hotbar_" + i, "Jutsu Slot " + i);
        t.add("key.narutorpg.transform", "Toggle Dojutsu");
        t.add("key.narutorpg.combo_release", "Release Hand Signs");
        for (Identifier sign : HandSigns.ALL) {
            t.add("key.narutorpg.combo_" + sign.getPath(), "Hand Sign: " + StringUtils.capitalize(sign.getPath()));
        }
    }
}
