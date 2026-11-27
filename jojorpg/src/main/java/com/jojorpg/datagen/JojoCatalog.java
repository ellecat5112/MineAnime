package com.jojorpg.datagen;

import com.animecore.quest.QuestDefinition;
import com.animecore.quest.StoryNpcDefinition;
import com.animecore.quest.StorySpawnDefinition;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.AffinityDefinition;
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

/** Reads tools/catalog/generated/jojo_catalog.json (tools/catalog/jojo_content.py + jojo_story.py) with the engine codecs. */
final class JojoCatalog {
    final Map<String, AffinityDefinition> affinities = new LinkedHashMap<>();
    final Map<String, LineageDefinition> lineages = new LinkedHashMap<>();
    final Map<String, AbilityDefinition> abilities = new LinkedHashMap<>();
    final Map<String, TransformationDefinition> transformations = new LinkedHashMap<>();
    final Map<String, QuestDefinition> quests = new LinkedHashMap<>();
    final Map<String, StoryNpcDefinition> npcs = new LinkedHashMap<>();
    final Map<String, StorySpawnDefinition> spawns = new LinkedHashMap<>();
    final Map<String, com.animecore.manifestation.ManifestationDefinition> manifestations = new LinkedHashMap<>();
    final Map<String, String> lang = new LinkedHashMap<>();

    private static JojoCatalog instance;

    static synchronized JojoCatalog get() {
        if (instance == null) {
            instance = new JojoCatalog();
            Path dir = locate();
            instance.load(dir.resolve("jojo_catalog.json"));
            instance.load(dir.resolve("jojo_story_catalog.json"));
            instance.load(dir.resolve("jojo_stands_catalog.json"));
            if (java.nio.file.Files.exists(dir.resolve("jojo_part3_story_catalog.json"))) instance.load(dir.resolve("jojo_part3_story_catalog.json"));
        }
        return instance;
    }

    private static Path locate() {
        for (Path dir = Path.of("").toAbsolutePath(); dir != null; dir = dir.getParent()) {
            Path p = dir.resolve("tools/catalog/generated");
            if (Files.isRegularFile(p.resolve("jojo_catalog.json"))) return p;
        }
        throw new IllegalStateException("jojo_catalog.json not found - run python3 tools/catalog/jojo_content.py");
    }

    private void load(Path path) {
        try (Reader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            decode(root, "affinities", AffinityDefinition.CODEC, affinities);
            decode(root, "lineages", LineageDefinition.CODEC, lineages);
            decode(root, "abilities", AbilityDefinition.CODEC, abilities);
            decode(root, "transformations", TransformationDefinition.CODEC, transformations);
            decode(root, "quests", QuestDefinition.CODEC, quests);
            decode(root, "npcs", StoryNpcDefinition.CODEC, npcs);
            decode(root, "spawns", StorySpawnDefinition.CODEC, spawns);
            decode(root, "manifestations", com.animecore.manifestation.ManifestationDefinition.CODEC, manifestations);
            if (root.has("lang")) root.getAsJsonObject("lang").entrySet().forEach(e -> lang.put(e.getKey(), e.getValue().getAsString()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + path, e);
        }
    }

    private static <T> void decode(JsonObject root, String section, Codec<T> codec, Map<String, T> out) {
        if (!root.has(section)) return;
        for (Map.Entry<String, JsonElement> e : root.getAsJsonObject(section).entrySet()) {
            out.put(e.getKey(), codec.parse(JsonOps.INSTANCE, e.getValue()).getOrThrow(msg -> new IllegalStateException(section + "/" + e.getKey() + ": " + msg)));
        }
    }
}
