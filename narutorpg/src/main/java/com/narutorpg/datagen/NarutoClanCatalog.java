package com.narutorpg.datagen;

import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.AffinityDefinition;
import com.animecore.registry.definition.BackgroundDefinition;
import com.animecore.registry.definition.FactionDefinition;
import com.animecore.registry.definition.LineageDefinition;
import com.animecore.registry.definition.TransformationDefinition;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DataGen-only reader for {@code tools/catalog/generated/clan_catalog.json} (written by {@code tools/catalog/clans.py}
 * from the Narutopedia {@code Category:Clans} crawl). Every entry is decoded with the engine codecs, so a bad entry
 * fails {@code runDatagen} instead of a world load. The file is not shipped in the jar.
 */
final class NarutoClanCatalog {
    private static NarutoClanCatalog instance;

    final Map<String, AffinityDefinition> affinities = new LinkedHashMap<>();
    final Map<String, TransformationDefinition> transformations = new LinkedHashMap<>();
    final Map<String, AbilityDefinition> abilities = new LinkedHashMap<>();
    final Map<String, LineageDefinition> lineages = new LinkedHashMap<>();
    final Map<String, FactionDefinition> factions = new LinkedHashMap<>();
    final Map<String, BackgroundDefinition> backgrounds = new LinkedHashMap<>();
    final Map<String, String> lang = new LinkedHashMap<>();
    final Map<String, com.animecore.quest.QuestDefinition> quests = new LinkedHashMap<>();
    final Map<String, com.animecore.quest.StoryNpcDefinition> npcs = new LinkedHashMap<>();

    static synchronized NarutoClanCatalog get() {
        if (instance == null) {
            Path clans = locate();
            instance = load(clans);
            instance.loadVillages(clans.resolveSibling("village_catalog.json"));
            instance.loadNatures(clans.resolveSibling("nature_catalog.json"));
            instance.loadNatures(clans.resolveSibling("fanon_genjutsu_catalog.json"));   // same shape: abilities + lang
            instance.loadNatures(clans.resolveSibling("taijutsu_catalog.json"));         // tools/catalog/taijutsu.py
            instance.loadNatures(clans.resolveSibling("ninjutsu_catalog.json"));         // tools/catalog/ninjutsu.py
            instance.loadNatures(clans.resolveSibling("schools_catalog.json"));          // tools/catalog/schools.py
            instance.loadStory(clans.resolveSibling("story_catalog.json"));              // tools/catalog/story.py
            instance.loadForms(clans.resolveSibling("forms_catalog.json"));              // tools/catalog/forms.py
            instance.loadLang(clans.resolveSibling("jutsu_descriptions.json"));          // tools/catalog/jutsu_descriptions.py
        }
        return instance;
    }

    private static Path locate() {
        String prop = System.getProperty("narutorpg.clanCatalog");
        if (prop != null) return Path.of(prop);
        for (Path dir = Path.of("").toAbsolutePath(); dir != null; dir = dir.getParent()) {
            Path p = dir.resolve("tools/catalog/generated/clan_catalog.json");
            if (Files.isRegularFile(p)) return p;
        }
        throw new IllegalStateException("clan_catalog.json not found - run python3 tools/catalog/clans.py");
    }

    private static NarutoClanCatalog load(Path path) {
        NarutoClanCatalog c = new NarutoClanCatalog();
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            decode(root, "affinities", AffinityDefinition.CODEC, c.affinities);
            decode(root, "transformations", TransformationDefinition.CODEC, c.transformations);
            decode(root, "abilities", AbilityDefinition.CODEC, c.abilities);
            decode(root, "lineages", LineageDefinition.CODEC, c.lineages);
            root.getAsJsonObject("lang").entrySet().forEach(e -> c.lang.put(e.getKey(), e.getValue().getAsString()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + path, e);
        }
        return c;
    }

    /** A catalog that only adds translations ({@code jutsu_descriptions.json}: every jutsu's description). */
    private void loadLang(Path path) {
        if (!Files.exists(path)) return;
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonParser.parseReader(r).getAsJsonObject().getAsJsonObject("lang").entrySet().forEach(e -> lang.put(e.getKey(), e.getValue().getAsString()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + path, e);
        }
    }

    /** {@code village_catalog.json} (tools/catalog/villages.py): villages, backgrounds, village techniques. */
    private void loadVillages(Path path) {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            decode(root, "factions", FactionDefinition.CODEC, factions);
            decode(root, "backgrounds", BackgroundDefinition.CODEC, backgrounds);
            decode(root, "abilities", AbilityDefinition.CODEC, abilities);
            root.getAsJsonObject("lang").entrySet().forEach(e -> lang.put(e.getKey(), e.getValue().getAsString()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + path + " - run python3 tools/catalog/villages.py", e);
        }
    }

    /** {@code nature_catalog.json} (tools/catalog/natures.py): every nature release jutsu. */
    /** {@code story_catalog.json} (tools/catalog/story.py): story quests + story characters + lang. */
    private void loadStory(Path path) {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            decode(root, "npcs", com.animecore.quest.StoryNpcDefinition.CODEC, npcs);
            decode(root, "quests", com.animecore.quest.QuestDefinition.CODEC, quests);
            root.getAsJsonObject("lang").entrySet().forEach(e -> lang.put(e.getKey(), e.getValue().getAsString()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + path + " - run python3 tools/catalog/story.py", e);
        }
    }

    /** {@code forms_catalog.json} (tools/catalog/forms.py): tailed beasts, curse marks, Kāma, modes, bloodline forms. */
    private void loadForms(Path path) {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            decode(root, "transformations", TransformationDefinition.CODEC, transformations);
            root.getAsJsonObject("lang").entrySet().forEach(e -> lang.put(e.getKey(), e.getValue().getAsString()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + path + " - run python3 tools/catalog/forms.py", e);
        }
    }

    private void loadNatures(Path path) {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            decode(root, "abilities", AbilityDefinition.CODEC, abilities);
            root.getAsJsonObject("lang").entrySet().forEach(e -> lang.put(e.getKey(), e.getValue().getAsString()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + path + " - run python3 tools/catalog/natures.py", e);
        }
    }

    private static <T> void decode(JsonObject root, String section, Codec<T> codec, Map<String, T> out) {
        for (Map.Entry<String, JsonElement> e : root.getAsJsonObject(section).entrySet()) {
            T value = codec.parse(JsonOps.INSTANCE, e.getValue())
                    .getOrThrow(msg -> new IllegalStateException(section + "/" + e.getKey() + ": " + msg));
            out.put(e.getKey(), value);
        }
    }
}
