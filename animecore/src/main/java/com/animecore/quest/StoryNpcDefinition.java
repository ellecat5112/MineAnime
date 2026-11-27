package com.animecore.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.List;
import java.util.Optional;

/**
 * A story character ({@code data/<ns>/animecore/story_npc/<id>.json}): quest givers, allies, enemies and bosses. Spawned
 * by the quest system as a {@link com.animecore.entity.StoryNpcEntity}; enemies fight with real abilities from the
 * ability registry (cast through {@link com.animecore.ability.AbilityContext#forMob}).
 *
 * <pre>{
 *   "system": "narutorpg", "name": {"translate": "npc.narutorpg.deidara", "fallback": "Deidara"},
 *   "role": "boss", "health": 120, "attack": 7, "power": 1.4,
 *   "abilities": ["narutorpg:explosive_clay", "narutorpg:c1"], "cast_interval": 50,
 *   "appearance": {"skin": 15845245, "hair": 16771899, "hair_style": "long", "outfit": "cloak", "top": 1315860, ...},
 *   "greeting": [{"text": "Art is an explosion!"}]
 * }</pre>
 */
public record StoryNpcDefinition(
        String system,
        Text name,
        Role role,
        float health,
        float attack,
        float speed,
        float power,
        float scale,
        List<Identifier> abilities,
        int castInterval,
        Appearance appearance,
        List<Text> greeting,
        Optional<Identifier> heldItem,
        Text title,
        boolean undead,
        Optional<Identifier> manifestation
) {
    public StoryNpcDefinition(String system, Text name, Role role, float health, float attack, float speed, float power, float scale,
                              List<Identifier> abilities, int castInterval, Appearance appearance, List<Text> greeting,
                              Optional<Identifier> heldItem, Text title, boolean undead) {
        this(system, name, role, health, attack, speed, power, scale, abilities, castInterval, appearance, greeting, heldItem, title, undead,
                Optional.empty());
    }

    public enum Role implements StringIdentifiable {
        GIVER("giver"), ALLY("ally"), ENEMY("enemy"), BOSS("boss");
        public static final Codec<Role> CODEC = StringIdentifiable.createCodec(Role::values);
        private final String name;
        Role(String name) { this.name = name; }
        @Override public String asString() { return name; }
        public boolean hostile() { return this == ENEMY || this == BOSS; }
    }

    /**
     * Procedural look: the renderer tints shared grayscale layer textures (skin, eyes, pants, outfit, hair, headband),
     * so any number of characters costs no extra assets.
     */
    public record Appearance(int skin, int eyes, int hair, String hairStyle, String outfit, int top, int accent, int pants,
                             Optional<Integer> headband, Optional<String> mark, Optional<Identifier> model) {
        public static final Appearance DEFAULT = new Appearance(0xF1C27D, 0x3B2A1A, 0x2B2B2B, "short", "shirt", 0x37474F,
                0x546E7A, 0x263238, Optional.empty(), Optional.empty(), Optional.empty());
        public static final Codec<Appearance> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.optionalFieldOf("skin", 0xF1C27D).forGetter(Appearance::skin),
                Codec.INT.optionalFieldOf("eyes", 0x3B2A1A).forGetter(Appearance::eyes),
                Codec.INT.optionalFieldOf("hair", 0x2B2B2B).forGetter(Appearance::hair),
                Codec.STRING.optionalFieldOf("hair_style", "short").forGetter(Appearance::hairStyle),
                Codec.STRING.optionalFieldOf("outfit", "shirt").forGetter(Appearance::outfit),
                Codec.INT.optionalFieldOf("top", 0x37474F).forGetter(Appearance::top),
                Codec.INT.optionalFieldOf("accent", 0x546E7A).forGetter(Appearance::accent),
                Codec.INT.optionalFieldOf("pants", 0x263238).forGetter(Appearance::pants),
                Codec.INT.optionalFieldOf("headband").forGetter(Appearance::headband),
                Codec.STRING.optionalFieldOf("mark").forGetter(Appearance::mark),
                Identifier.CODEC.optionalFieldOf("model").forGetter(Appearance::model)   // low-poly form model instead of a humanoid
        ).apply(i, Appearance::new));
    }

    public static final Codec<StoryNpcDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(StoryNpcDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(StoryNpcDefinition::name),
            Role.CODEC.optionalFieldOf("role", Role.ENEMY).forGetter(StoryNpcDefinition::role),
            Codec.floatRange(1f, 2048f).optionalFieldOf("health", 20f).forGetter(StoryNpcDefinition::health),
            Codec.floatRange(0f, 200f).optionalFieldOf("attack", 3f).forGetter(StoryNpcDefinition::attack),
            Codec.floatRange(0f, 1f).optionalFieldOf("speed", 0.3f).forGetter(StoryNpcDefinition::speed),
            Codec.floatRange(0f, 20f).optionalFieldOf("power", 1f).forGetter(StoryNpcDefinition::power),
            Codec.floatRange(0.3f, 4f).optionalFieldOf("scale", 1f).forGetter(StoryNpcDefinition::scale),
            Identifier.CODEC.listOf().optionalFieldOf("abilities", List.of()).forGetter(StoryNpcDefinition::abilities),
            Codec.intRange(10, 1200).optionalFieldOf("cast_interval", 60).forGetter(StoryNpcDefinition::castInterval),
            Appearance.CODEC.optionalFieldOf("appearance", Appearance.DEFAULT).forGetter(StoryNpcDefinition::appearance),
            TextCodecs.CODEC.listOf().optionalFieldOf("greeting", List.of()).forGetter(StoryNpcDefinition::greeting),
            Identifier.CODEC.optionalFieldOf("held_item").forGetter(StoryNpcDefinition::heldItem),
            TextCodecs.CODEC.optionalFieldOf("title", Text.empty()).forGetter(StoryNpcDefinition::title),
            Codec.BOOL.optionalFieldOf("undead", false).forGetter(StoryNpcDefinition::undead),   // weak to Hamon / holy affinities
            Identifier.CODEC.optionalFieldOf("manifestation").forGetter(StoryNpcDefinition::manifestation)   // their Stand, called out in battle
    ).apply(i, StoryNpcDefinition::new));
}
