package com.narutorpg.datagen;

import com.narutorpg.content.HandSigns;
import com.narutorpg.content.NarutoTools;
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
        // Clans, clan jutsu, dojutsu and advanced natures (from the Narutopedia clan catalog).
        NarutoClanCatalog.get().lang.forEach(t::add);
        t.add("item.narutorpg.clan_emblem", "Clan Emblem");
        t.add("item.narutorpg.clan_garb", "Clan Garb");
        t.add("item.narutorpg.bloodline_scroll", "Bloodline Scroll");
        t.add("item.narutorpg.forehead_protector", "Forehead Protector");
        t.add("item.narutorpg.flak_jacket", "Flak Jacket");
        t.add("item.narutorpg.forged_papers", "Forged Papers");
        com.narutorpg.content.NarutoTokens.names().forEach(t::add);
        com.narutorpg.content.NarutoAdmin.SEALS.forEach((a, item) -> t.add(item, com.narutorpg.content.NarutoAdmin.NAMES.get(a)));
        t.add(com.narutorpg.content.NarutoStory.QUEST_SCROLL, "Quest Scroll");
        t.add("message.narutorpg.quest_scroll", "You received a Quest Scroll - use it to play through the story of Naruto, Shippuden, Boruto and the movies.");
        t.add("itemGroup.narutorpg.admin", "NarutoRPG Admin");
        t.add("ability.narutorpg.fireball_jutsu", "Fire Release: Great Fireball Technique");
        t.add("ability.narutorpg.substitution_jutsu", "Substitution Jutsu");
        t.add("transformation.narutorpg.sharingan_one_tomoe", "Sharingan (1 Tomoe)");
        t.add("transformation.narutorpg.sharingan_two_tomoe", "Sharingan (2 Tomoe)");
        t.add("transformation.narutorpg.sharingan_three_tomoe", "Sharingan (3 Tomoe)");
        t.add("transformation.narutorpg.mangekyo_sharingan", "Mangekyo Sharingan");
        t.add("message.narutorpg.fire_underwater", "Your Fire Release fizzles underwater");

        t.add("item.narutorpg.chakra_blade", "Chakra Blade");
        t.add("item.narutorpg.jutsu_scroll", "Jutsu Scroll");
        t.add("item.narutorpg.leaf_headband", "Hidden Leaf Headband");
        t.add("itemGroup.narutorpg", "NarutoRPG");
        for (String group : NarutoTools.GROUPS) {
            t.add("itemGroup.narutorpg.tools_" + group.toLowerCase(), "Ninja Tools: " + group);
        }
        NarutoTools.specs().forEach((id, spec) -> {
            if (!NarutoTools.items().containsKey(id)) return;
            t.add("item.narutorpg." + id, spec.name());
            if (!spec.description().isEmpty()) t.add("item.narutorpg." + id + ".desc", spec.description());
        });
        t.add("pool.narutorpg.chakra", "Chakra");
        t.add("pool.narutorpg.stamina", "Stamina");
        t.add("key.categories.narutorpg", "NarutoRPG");
        t.add("key.narutorpg.combo_wheel", "Hand-Sign Wheel (hold)");
        t.add("key.narutorpg.transform_ascend", "Dojutsu: Next Stage");
        t.add("key.narutorpg.transform_descend", "Dojutsu: Previous Stage");
        t.add("key.narutorpg.movement_surface_adhesion", "Chakra Control (Tree/Water Walking)");
        t.add("key.narutorpg.movement_burst_step", "Body Flicker (Shunshin)");
        for (int i = 1; i <= 3; i++) t.add("key.narutorpg.hotbar_" + i, "Jutsu Slot " + i);
        t.add("key.narutorpg.transform", "Toggle Dojutsu");
        t.add("key.narutorpg.combo_release", "Release Hand Signs");
        for (Identifier sign : HandSigns.ALL) {
            t.add("key.narutorpg.combo_" + sign.getPath(), "Hand Sign: " + StringUtils.capitalize(sign.getPath()));
            t.add("combo.narutorpg." + sign.getPath(), StringUtils.capitalize(sign.getPath()));
        }
    }
}
