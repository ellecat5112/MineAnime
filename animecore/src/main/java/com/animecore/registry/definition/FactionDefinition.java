package com.animecore.registry.definition;

import com.animecore.trait.ITraitModifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Faction / origin (Hidden Leaf, Hidden Sand, Missing-nin, Soul Society, Capsule Corp...).
 * {@code data/<ns>/animecore/faction/<id>.json}
 *
 * <pre>{@code
 * {
 *   "system": "narutorpg", "name": {...}, "color": 5025616, "kind": "hidden_village",
 *   "selectable": true,                              // selectable on the first-join origin screen
 *   "traits": [...],                                 // village perks (stat modifiers, removed when leaving)
 *   "starting_abilities": ["narutorpg:substitution_jutsu"],
 *   "jutsu_pool": ["narutorpg:shadow_clone_technique", ...],   // village techniques, unlocked by rank for members
 *   "kit": {"weapons": [ItemStack...], "clothing": [ItemStack...]},
 *   "exclusive": {"items": ["narutorpg:hiramekarei"], "abilities": [...], "allied_reputation": 50, "outsider_tier": 5},
 *   "reputation": {"narutorpg:hidden_sand": 50, "narutorpg:hidden_cloud": -25},   // starting standings
 *   "rogue": false
 * }
 * }</pre>
 *
 * @param startingReputation reputation with this faction itself on joining
 * @param allies / rivals    legacy relation lists (kept for older datapacks; {@code reputation} supersedes them)
 */
public record FactionDefinition(
        String system,
        Text name,
        int color,
        List<ITraitModifier> traits,
        List<Identifier> startingAbilities,
        List<Identifier> allies,
        List<Identifier> rivals,
        int startingReputation,
        boolean selectable,
        Text description,
        String kind,
        StarterKit kit,
        List<Identifier> jutsuPool,
        Exclusives exclusive,
        Map<Identifier, Integer> reputation,
        boolean rogue
) {
    /** Legacy constructor (pre-origins). */
    public FactionDefinition(String system, Text name, int color, List<ITraitModifier> traits, List<Identifier> startingAbilities,
                             List<Identifier> allies, List<Identifier> rivals, int startingReputation, boolean selectable) {
        this(system, name, color, traits, startingAbilities, allies, rivals, startingReputation, selectable, Text.empty(),
                "faction", StarterKit.EMPTY, List.of(), Exclusives.NONE, Map.of(), false);
    }

    /** Starting gear handed out when the origin is chosen (stacks carry their data components). */
    public record StarterKit(List<ItemStack> weapons, List<ItemStack> clothing) {
        public static final StarterKit EMPTY = new StarterKit(List.of(), List.of());
        public static final Codec<StarterKit> CODEC = RecordCodecBuilder.create(i -> i.group(
                ItemStack.CODEC.listOf().optionalFieldOf("weapons", List.of()).forGetter(StarterKit::weapons),
                ItemStack.CODEC.listOf().optionalFieldOf("clothing", List.of()).forGetter(StarterKit::clothing)
        ).apply(i, StarterKit::new));
    }

    /**
     * What belongs to this faction. Outsiders lose full use of exclusive gear and can only learn exclusive / pool
     * techniques from captured scrolls at {@code outsider_tier}, unless their reputation reaches {@code allied_reputation}.
     */
    public record Exclusives(List<Identifier> items, Optional<TagKey<Item>> itemTag, List<Identifier> abilities,
                             int alliedReputation, int outsiderTier) {
        public static final Exclusives NONE = new Exclusives(List.of(), Optional.empty(), List.of(), 50, 5);
        public static final Codec<Exclusives> CODEC = RecordCodecBuilder.create(i -> i.group(
                Identifier.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(Exclusives::items),
                TagKey.codec(RegistryKeys.ITEM).optionalFieldOf("item_tag").forGetter(Exclusives::itemTag),
                Identifier.CODEC.listOf().optionalFieldOf("abilities", List.of()).forGetter(Exclusives::abilities),
                Codec.intRange(-1000, 1000).optionalFieldOf("allied_reputation", 50).forGetter(Exclusives::alliedReputation),
                Codec.intRange(1, 1000).optionalFieldOf("outsider_tier", 5).forGetter(Exclusives::outsiderTier)
        ).apply(i, Exclusives::new));
    }

    public static final Codec<FactionDefinition> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("system").forGetter(FactionDefinition::system),
            TextCodecs.CODEC.fieldOf("name").forGetter(FactionDefinition::name),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(FactionDefinition::color),
            ITraitModifier.CODEC.listOf().optionalFieldOf("traits", List.of()).forGetter(FactionDefinition::traits),
            Identifier.CODEC.listOf().optionalFieldOf("starting_abilities", List.of()).forGetter(FactionDefinition::startingAbilities),
            Identifier.CODEC.listOf().optionalFieldOf("allies", List.of()).forGetter(FactionDefinition::allies),
            Identifier.CODEC.listOf().optionalFieldOf("rivals", List.of()).forGetter(FactionDefinition::rivals),
            Codec.INT.optionalFieldOf("starting_reputation", 0).forGetter(FactionDefinition::startingReputation),
            Codec.BOOL.optionalFieldOf("selectable", true).forGetter(FactionDefinition::selectable),
            TextCodecs.CODEC.optionalFieldOf("description", Text.empty()).forGetter(FactionDefinition::description),
            Codec.STRING.optionalFieldOf("kind", "faction").forGetter(FactionDefinition::kind),
            StarterKit.CODEC.optionalFieldOf("kit", StarterKit.EMPTY).forGetter(FactionDefinition::kit),
            Identifier.CODEC.listOf().optionalFieldOf("jutsu_pool", List.of()).forGetter(FactionDefinition::jutsuPool),
            Exclusives.CODEC.optionalFieldOf("exclusive", Exclusives.NONE).forGetter(FactionDefinition::exclusive),
            Codec.unboundedMap(Identifier.CODEC, Codec.INT).optionalFieldOf("reputation", Map.of()).forGetter(FactionDefinition::reputation),
            Codec.BOOL.optionalFieldOf("rogue", false).forGetter(FactionDefinition::rogue)
    ).apply(i, FactionDefinition::new));
}
