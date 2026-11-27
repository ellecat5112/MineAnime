package com.animecore.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * A story character that also roams the world ({@code data/<ns>/animecore/story_spawn/<id>.json}): Dio's zombies at
 * night, vampire knights near ruins, rogue ninja on the roads... Spawned near survival players by {@link StorySpawner}
 * and despawned again when nobody is around.
 *
 * <pre>{"system": "jojorpg", "npc": "jojorpg:zombie", "weight": 20, "group": [2, 4], "night_only": true, "max_light": 7,
 *  "min_tier": 1, "max_nearby": 6, "dimensions": ["minecraft:overworld"]}</pre>
 */
public record StorySpawnDefinition(String system, Identifier npc, int weight, List<Integer> group, boolean nightOnly, int maxLight,
                                   int minTier, int maxNearby, List<Identifier> dimensions) {
    public static final Codec<StorySpawnDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(StorySpawnDefinition::system),
            Identifier.CODEC.fieldOf("npc").forGetter(StorySpawnDefinition::npc),
            Codec.intRange(1, 1000).optionalFieldOf("weight", 10).forGetter(StorySpawnDefinition::weight),
            Codec.INT.listOf().optionalFieldOf("group", List.of(1, 1)).forGetter(StorySpawnDefinition::group),
            Codec.BOOL.optionalFieldOf("night_only", true).forGetter(StorySpawnDefinition::nightOnly),
            Codec.intRange(0, 15).optionalFieldOf("max_light", 7).forGetter(StorySpawnDefinition::maxLight),
            Codec.intRange(0, 1000).optionalFieldOf("min_tier", 0).forGetter(StorySpawnDefinition::minTier),
            Codec.intRange(1, 64).optionalFieldOf("max_nearby", 6).forGetter(StorySpawnDefinition::maxNearby),
            Identifier.CODEC.listOf().optionalFieldOf("dimensions", List.of(Identifier.of("minecraft", "overworld"))).forGetter(StorySpawnDefinition::dimensions)
    ).apply(i, StorySpawnDefinition::new));
}
