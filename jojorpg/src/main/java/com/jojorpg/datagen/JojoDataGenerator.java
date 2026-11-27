package com.jojorpg.datagen;

import com.animecore.registry.AnimeRegistries;
import com.jojorpg.JojoRPG;
import com.jojorpg.JojoText;
import com.jojorpg.content.JojoItems;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/** {@code ./gradlew :jojorpg:runDatagen}: content registries, lang and item models from the JoJo catalog. */
public final class JojoDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(Content::new);
        pack.addProvider(Lang::new);
        pack.addProvider(ItemModels::new);
    }

    private static <T> void register(Registerable<T> ctx, RegistryKey<? extends Registry<T>> registry, Map<String, T> entries) {
        entries.forEach((path, def) -> ctx.register(RegistryKey.of(registry, JojoRPG.CTX.id(path)), def));
    }

    @Override
    public void buildRegistry(RegistryBuilder builder) {
        builder.addRegistry(AnimeRegistries.AFFINITY, c -> register(c, AnimeRegistries.AFFINITY, JojoCatalog.get().affinities));
        builder.addRegistry(AnimeRegistries.LINEAGE, c -> register(c, AnimeRegistries.LINEAGE, JojoCatalog.get().lineages));
        builder.addRegistry(AnimeRegistries.ABILITY, c -> register(c, AnimeRegistries.ABILITY, JojoCatalog.get().abilities));
        builder.addRegistry(AnimeRegistries.TRANSFORMATION, c -> register(c, AnimeRegistries.TRANSFORMATION, JojoCatalog.get().transformations));
        builder.addRegistry(AnimeRegistries.QUEST, c -> register(c, AnimeRegistries.QUEST, JojoCatalog.get().quests));
        builder.addRegistry(AnimeRegistries.STORY_NPC, c -> register(c, AnimeRegistries.STORY_NPC, JojoCatalog.get().npcs));
        builder.addRegistry(AnimeRegistries.STORY_SPAWN, c -> register(c, AnimeRegistries.STORY_SPAWN, JojoCatalog.get().spawns));
        builder.addRegistry(AnimeRegistries.MANIFESTATION, c -> register(c, AnimeRegistries.MANIFESTATION, JojoCatalog.get().manifestations));
    }

    private static final class Content extends FabricDynamicRegistryProvider {
        Content(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) { super(output, registries); }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.AFFINITY));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.LINEAGE));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.ABILITY));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.TRANSFORMATION));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.QUEST));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.STORY_NPC));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.STORY_SPAWN));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.MANIFESTATION));
        }

        @Override public String getName() { return "JoJoRPG content"; }
    }

    private static final class Lang extends FabricLanguageProvider {
        Lang(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) { super(output, "en_us", registries); }

        @Override
        public void generateTranslations(RegistryWrapper.WrapperLookup registries, TranslationBuilder t) {
            JojoCatalog.get().lang.forEach(t::add);
            JojoText.ENGINE.forEach((k, v) -> t.add(JojoText.key(k), v));
            for (String[] tok : JojoItems.TOKENS) t.add(JojoItems.TOKEN_ITEMS.get(JojoRPG.CTX.id(tok[0])), tok[1]);
            t.add(JojoItems.STYLE_MANUAL, "Style Manual");
            t.add(JojoItems.QUEST_SCROLL, "JoJo's Bizarre Journal");
            Map<String, String> seals = Map.of("rank_up", "Rank Up Seal", "clan_level", "Style Mastery Seal", "mastery", "Mastery Seal",
                    "skill_points", "Skill Point Seal", "natures", "Affinity Seal", "reputation", "Reputation Seal", "refill", "Ripple Seal",
                    "max_all", "Joestar Seal", "story", "Story Seal");
            JojoItems.SEALS.forEach((a, item) -> t.add(item, seals.getOrDefault(a.key(), a.key())));
            t.add("itemGroup.jojorpg", "JoJoRPG");
            t.add("itemGroup.jojorpg.admin", "JoJoRPG Admin");
            t.add("message.jojorpg.quest_scroll", "You received JoJo's Bizarre Journal - use it to play through Phantom Blood and Battle Tendency.");
            t.add("pool.jojorpg.ripple", "Ripple");
            t.add("pool.jojorpg.stamina", "Stamina");
            t.add("pool.jojorpg.blood", "Blood");
            t.add("pool.jojorpg.stand", "Stand Energy");
            t.add("itemGroup.jojorpg.stands", "JoJoRPG Stands");
        }
    }

    private static final class ItemModels extends FabricModelProvider {
        ItemModels(FabricDataOutput output) { super(output); }
        @Override public void generateBlockStateModels(BlockStateModelGenerator generator) { }
        @Override
        public void generateItemModels(ItemModelGenerator generator) {
            JojoItems.TOKEN_ITEMS.values().forEach(i -> generator.register(i, Models.GENERATED));
            JojoItems.AWAKENING.values().forEach(i -> generator.register(i, Models.GENERATED));
            JojoItems.SEALS.values().forEach(i -> generator.register(i, Models.GENERATED));
            JojoItems.STAND_ITEMS.values().forEach(i -> generator.register(i, Models.GENERATED));
            generator.register(JojoItems.STYLE_MANUAL, Models.GENERATED);
            generator.register(JojoItems.QUEST_SCROLL, Models.GENERATED);
        }
    }
}
