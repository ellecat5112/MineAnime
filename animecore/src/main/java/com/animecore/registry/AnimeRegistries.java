package com.animecore.registry;

import com.animecore.platform.AnimeRegistryAccess;
import com.animecore.platform.Ids;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.AffinityDefinition;
import com.animecore.registry.definition.FactionDefinition;
import com.animecore.registry.definition.LineageDefinition;
import com.animecore.registry.definition.TransformationDefinition;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * Shared, synced, data-driven registries.
 * <p>
 * The <b>registries</b> are owned by the engine ({@code animecore:ability} ...) so that every anime mod shares one
 * clash matrix, one ability system and one event pipeline. Every <b>entry</b> is namespaced by its content mod
 * ({@code narutorpg:fireball_jutsu}, {@code bleachrpg:getsuga_tensho}), so mods never collide.
 * <p>
 * JSON lives at {@code data/<entry-namespace>/animecore/<registry>/<path>.json}.
 */
public final class AnimeRegistries {
    private AnimeRegistries() {}

    public static final RegistryKey<Registry<AbilityDefinition>> ABILITY = RegistryKey.ofRegistry(Ids.core("ability"));
    public static final RegistryKey<Registry<LineageDefinition>> LINEAGE = RegistryKey.ofRegistry(Ids.core("lineage"));
    public static final RegistryKey<Registry<FactionDefinition>> FACTION = RegistryKey.ofRegistry(Ids.core("faction"));
    public static final RegistryKey<Registry<AffinityDefinition>> AFFINITY = RegistryKey.ofRegistry(Ids.core("affinity"));
    public static final RegistryKey<Registry<TransformationDefinition>> TRANSFORMATION = RegistryKey.ofRegistry(Ids.core("transformation"));
    public static final RegistryKey<Registry<com.animecore.registry.definition.BackgroundDefinition>> BACKGROUND = RegistryKey.ofRegistry(Ids.core("background"));

    public static final RegistryKey<Registry<com.animecore.quest.QuestDefinition>> QUEST = RegistryKey.ofRegistry(Ids.core("quest"));
    public static final RegistryKey<Registry<com.animecore.quest.StoryNpcDefinition>> STORY_NPC = RegistryKey.ofRegistry(Ids.core("story_npc"));
    public static final RegistryKey<Registry<com.animecore.manifestation.ManifestationDefinition>> MANIFESTATION = RegistryKey.ofRegistry(Ids.core("manifestation"));
    public static final RegistryKey<Registry<com.animecore.quest.StorySpawnDefinition>> STORY_SPAWN = RegistryKey.ofRegistry(Ids.core("story_spawn"));

    public static void init() {
        // Synced so clients can render names, colors, combos and HUD icons without extra packets.
        DynamicRegistries.registerSynced(ABILITY, AbilityDefinition.CODEC);
        DynamicRegistries.registerSynced(LINEAGE, LineageDefinition.CODEC);
        DynamicRegistries.registerSynced(FACTION, FactionDefinition.CODEC);
        DynamicRegistries.registerSynced(AFFINITY, AffinityDefinition.CODEC);
        DynamicRegistries.registerSynced(TRANSFORMATION, TransformationDefinition.CODEC);
        DynamicRegistries.registerSynced(BACKGROUND, com.animecore.registry.definition.BackgroundDefinition.CODEC);
        DynamicRegistries.registerSynced(QUEST, com.animecore.quest.QuestDefinition.CODEC);
        DynamicRegistries.registerSynced(STORY_NPC, com.animecore.quest.StoryNpcDefinition.CODEC);
        DynamicRegistries.registerSynced(MANIFESTATION, com.animecore.manifestation.ManifestationDefinition.CODEC);
        DynamicRegistries.register(STORY_SPAWN, com.animecore.quest.StorySpawnDefinition.CODEC);   // server-side only
    }

    // ---- convenience lookups (version-shielded) ----

    public static Optional<RegistryEntry.Reference<AbilityDefinition>> ability(DynamicRegistryManager m, Identifier id) {
        return AnimeRegistryAccess.entry(m, ABILITY, id);
    }

    public static Optional<RegistryEntry.Reference<TransformationDefinition>> transformation(DynamicRegistryManager m, Identifier id) {
        return AnimeRegistryAccess.entry(m, TRANSFORMATION, id);
    }

    public static Optional<LineageDefinition> lineage(DynamicRegistryManager m, Identifier id) {
        return AnimeRegistryAccess.value(m, LINEAGE, id);
    }

    public static Optional<FactionDefinition> faction(DynamicRegistryManager m, Identifier id) {
        return AnimeRegistryAccess.value(m, FACTION, id);
    }

    public static Optional<com.animecore.registry.definition.BackgroundDefinition> background(DynamicRegistryManager m, Identifier id) {
        return AnimeRegistryAccess.value(m, BACKGROUND, id);
    }

    public static Optional<com.animecore.quest.QuestDefinition> quest(DynamicRegistryManager m, Identifier id) {
        return m.getOptional(QUEST).flatMap(r -> r.getOrEmpty(id));
    }

    public static Optional<com.animecore.quest.StoryNpcDefinition> storyNpc(DynamicRegistryManager m, Identifier id) {
        return m.getOptional(STORY_NPC).flatMap(r -> r.getOrEmpty(id));
    }

    public static Optional<com.animecore.manifestation.ManifestationDefinition> manifestation(DynamicRegistryManager m, Identifier id) {
        return m.getOptional(MANIFESTATION).flatMap(r -> r.getOrEmpty(id));
    }

    public static Registry<AffinityDefinition> affinities(DynamicRegistryManager m) {
        return AnimeRegistryAccess.registry(m, AFFINITY);
    }
}
