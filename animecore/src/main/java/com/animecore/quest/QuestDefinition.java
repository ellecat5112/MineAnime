package com.animecore.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.List;
import java.util.Optional;

/**
 * One story quest ({@code data/<ns>/animecore/quest/<id>.json}). Quests are grouped into arcs and series for the quest
 * journal, unlock through {@code requires}, and run their objectives in order. Everything is data: a content mod declares
 * quests, the engine spawns givers / allies / enemies ({@link StoryNpcDefinition}), tracks progress per player and pays the
 * rewards.
 *
 * <pre>{
 *   "system": "narutorpg", "name": {...}, "description": {...},
 *   "series": "naruto", "arc": {...}, "order": 120, "requires": ["narutorpg:land_of_waves_1"], "recommended_tier": 2,
 *   "giver": "narutorpg:kakashi_hatake", "allies": ["narutorpg:sakura_haruno"],
 *   "intro": [{...}], "outro": [{...}],
 *   "objectives": [
 *     {"type": "talk", "target": "narutorpg:tazuna", "text": {...}},
 *     {"type": "travel", "count": 80, "text": {...}},
 *     {"type": "defeat", "target": "narutorpg:zabuza_momochi", "text": {...}}
 *   ],
 *   "rewards": {"tier": 3, "skill_points": 2, "items": [{"id": "narutorpg:kunai", "count": 8}]}
 * }</pre>
 */
public record QuestDefinition(
        String system,
        Text name,
        Text description,
        String series,
        Text arc,
        int order,
        List<Identifier> requires,
        int recommendedTier,
        Optional<Identifier> giver,
        List<Identifier> allies,
        List<Text> intro,
        List<Text> outro,
        List<Objective> objectives,
        Rewards rewards
) {
    public enum ObjectiveType implements StringIdentifiable {
        /** Speak to {@code target} (a story NPC that appears beside you, or at the encounter site after a travel step). */
        TALK("talk"),
        /** Defeat {@code count} copies of story NPC {@code target}; they appear at the encounter site / ahead of you. */
        DEFEAT("defeat"),
        /** Kill {@code count} mobs of entity type {@code target}. */
        KILL("kill"),
        /** Hold {@code count} of item {@code target} in your inventory. */
        COLLECT("collect"),
        /** Cast {@code count} techniques ({@code target} = an ability, technique token or affinity; absent = any). */
        USE_ABILITY("use_ability"),
        /** Travel to an encounter site about {@code count} blocks away (marked on the HUD). */
        TRAVEL("travel"),
        /** Reach rank {@code count}. */
        RANK("rank");

        public static final Codec<ObjectiveType> CODEC = StringIdentifiable.createCodec(ObjectiveType::values);
        private final String name;
        ObjectiveType(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }

    public record Objective(ObjectiveType type, Optional<Identifier> target, int count, Text text) {
        public static final Codec<Objective> CODEC = RecordCodecBuilder.create(i -> i.group(
                ObjectiveType.CODEC.fieldOf("type").forGetter(Objective::type),
                Identifier.CODEC.optionalFieldOf("target").forGetter(Objective::target),
                Codec.intRange(1, 100000).optionalFieldOf("count", 1).forGetter(Objective::count),
                TextCodecs.CODEC.optionalFieldOf("text", Text.empty()).forGetter(Objective::text)
        ).apply(i, Objective::new));
    }

    public record Rewards(int tier, int skillPoints, int experience, List<ItemStack> items, List<Identifier> abilities) {
        public static final Rewards NONE = new Rewards(0, 0, 0, List.of(), List.of());
        public static final Codec<Rewards> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.intRange(0, 1000).optionalFieldOf("tier", 0).forGetter(Rewards::tier),
                Codec.intRange(0, 1000).optionalFieldOf("skill_points", 0).forGetter(Rewards::skillPoints),
                Codec.intRange(0, 100000).optionalFieldOf("experience", 0).forGetter(Rewards::experience),
                ItemStack.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(Rewards::items),
                Identifier.CODEC.listOf().optionalFieldOf("abilities", List.of()).forGetter(Rewards::abilities)
        ).apply(i, Rewards::new));
    }

    public static final Codec<QuestDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(QuestDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(QuestDefinition::name),
            TextCodecs.CODEC.optionalFieldOf("description", Text.empty()).forGetter(QuestDefinition::description),
            Codec.STRING.optionalFieldOf("series", "main").forGetter(QuestDefinition::series),
            TextCodecs.CODEC.optionalFieldOf("arc", Text.empty()).forGetter(QuestDefinition::arc),
            Codec.INT.optionalFieldOf("order", 0).forGetter(QuestDefinition::order),
            Identifier.CODEC.listOf().optionalFieldOf("requires", List.of()).forGetter(QuestDefinition::requires),
            Codec.intRange(1, 1000).optionalFieldOf("recommended_tier", 1).forGetter(QuestDefinition::recommendedTier),
            Identifier.CODEC.optionalFieldOf("giver").forGetter(QuestDefinition::giver),
            Identifier.CODEC.listOf().optionalFieldOf("allies", List.of()).forGetter(QuestDefinition::allies),
            TextCodecs.CODEC.listOf().optionalFieldOf("intro", List.of()).forGetter(QuestDefinition::intro),
            TextCodecs.CODEC.listOf().optionalFieldOf("outro", List.of()).forGetter(QuestDefinition::outro),
            Objective.CODEC.listOf().fieldOf("objectives").forGetter(QuestDefinition::objectives),
            Rewards.CODEC.optionalFieldOf("rewards", Rewards.NONE).forGetter(QuestDefinition::rewards)
    ).apply(i, QuestDefinition::new));
}
