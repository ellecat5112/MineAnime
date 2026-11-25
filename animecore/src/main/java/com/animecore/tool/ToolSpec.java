package com.animecore.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;

import java.util.List;
import java.util.Optional;

/**
 * Data definition of one ninja tool (loaded from a content mod's catalog JSON at startup).
 *
 * @param classification wiki drill-down groups (Offensive / Defensive / Supplementary / Unclassified / Generic)
 * @param primary        right-click move
 * @param secondary      sneak + right-click move
 * @param armor          wearable slot, if any (right-click still equips; sneak-use performs the secondary move)
 * @param worn           effects refreshed while worn
 */
public record ToolSpec(String id, String name, String wiki, List<String> classification, AttackProfile attack,
                       Optional<MoveSpec> primary, Optional<MoveSpec> secondary, int maxStack, int durability,
                       String rarity, Optional<Armor> armor, List<ToolEffect> worn, String description) {

    public enum Slot implements StringIdentifiable {
        HEAD, CHEST, LEGS, FEET;
        public static final Codec<Slot> CODEC = StringIdentifiable.createCodec(Slot::values);
        @Override public String asString() { return name().toLowerCase(java.util.Locale.ROOT); }
    }

    public record Armor(Slot slot, int defense, int color) {
        public static final Codec<Armor> CODEC = RecordCodecBuilder.create(i -> i.group(
                Slot.CODEC.fieldOf("slot").forGetter(Armor::slot),
                Codec.intRange(0, 20).optionalFieldOf("defense", 2).forGetter(Armor::defense),
                Codec.INT.optionalFieldOf("color", 0x3A5F3A).forGetter(Armor::color)
        ).apply(i, Armor::new));
    }

    public static final Codec<ToolSpec> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("id").forGetter(ToolSpec::id),
            Codec.STRING.fieldOf("name").forGetter(ToolSpec::name),
            Codec.STRING.optionalFieldOf("wiki", "").forGetter(ToolSpec::wiki),
            Codec.STRING.listOf().optionalFieldOf("classification", List.of()).forGetter(ToolSpec::classification),
            AttackProfile.CODEC.optionalFieldOf("attack", AttackProfile.FIST).forGetter(ToolSpec::attack),
            MoveSpec.CODEC.optionalFieldOf("primary").forGetter(ToolSpec::primary),
            MoveSpec.CODEC.optionalFieldOf("secondary").forGetter(ToolSpec::secondary),
            Codec.intRange(1, 64).optionalFieldOf("max_stack", 1).forGetter(ToolSpec::maxStack),
            Codec.intRange(0, 10_000).optionalFieldOf("durability", 0).forGetter(ToolSpec::durability),
            Codec.STRING.optionalFieldOf("rarity", "common").forGetter(ToolSpec::rarity),
            Armor.CODEC.optionalFieldOf("armor").forGetter(ToolSpec::armor),
            ToolEffect.CODEC.listOf().optionalFieldOf("worn", List.of()).forGetter(ToolSpec::worn),
            Codec.STRING.optionalFieldOf("description", "").forGetter(ToolSpec::description)
    ).apply(i, ToolSpec::new));

    public static final Codec<List<ToolSpec>> LIST_CODEC = CODEC.listOf();
}
