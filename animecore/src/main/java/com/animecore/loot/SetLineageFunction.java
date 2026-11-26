package com.animecore.loot;

import com.animecore.clan.ClanRollService;
import com.animecore.item.AnimeDataComponents;
import com.animecore.registry.AnimeRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

/**
 * {@code "function": "animecore:set_lineage"}: binds an emblem / garb / bloodline scroll to a clan, either a fixed
 * {@code lineage} or a weighted roll among the rollable lineages of {@code system} ({@code rare_only} limits the roll
 * to {@code weight <= rare_weight}). Dyeable items also receive the clan colour.
 * <p>
 * Uses {@link LootContext#getRandom()}, so Lootr's per-player containers each roll their own clan; no Lootr API needed.
 */
public final class SetLineageFunction extends ConditionalLootFunction {
    public static final MapCodec<SetLineageFunction> CODEC = RecordCodecBuilder.mapCodec(i -> addConditionsField(i).and(i.group(
            Identifier.CODEC.optionalFieldOf("lineage").forGetter(f -> f.lineage),
            Codec.STRING.optionalFieldOf("system").forGetter(f -> f.system),
            Codec.BOOL.optionalFieldOf("rare_only", false).forGetter(f -> f.rareOnly),
            Codec.INT.optionalFieldOf("rare_weight", 3).forGetter(f -> f.rareWeight)
    )).apply(i, SetLineageFunction::new));

    private final Optional<Identifier> lineage;
    private final Optional<String> system;
    private final boolean rareOnly;
    private final int rareWeight;

    private SetLineageFunction(List<LootCondition> conditions, Optional<Identifier> lineage, Optional<String> system,
                               boolean rareOnly, int rareWeight) {
        super(conditions);
        this.lineage = lineage;
        this.system = system;
        this.rareOnly = rareOnly;
        this.rareWeight = rareWeight;
    }

    @Override
    public LootFunctionType<SetLineageFunction> getType() {
        return AnimeLootFunctions.SET_LINEAGE;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        var rm = context.getWorld().getRegistryManager();
        Optional<Identifier> chosen = lineage.or(() -> system.flatMap(s ->
                ClanRollService.roll(rm, s, context.getRandom(), null, rareOnly, rareWeight)));
        chosen.ifPresent(id -> {
            stack.set(AnimeDataComponents.CLAN_BINDING, new AnimeDataComponents.ClanBinding(id));
            if (stack.isIn(ItemTags.DYEABLE)) {
                AnimeRegistries.lineage(rm, id).ifPresent(def ->
                        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(def.color() & 0xFFFFFF, false)));
            }
        });
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> fixed(Identifier lineage) {
        return builder(c -> new SetLineageFunction(c, Optional.of(lineage), Optional.empty(), false, 3));
    }

    public static ConditionalLootFunction.Builder<?> random(String system, boolean rareOnly, int rareWeight) {
        return builder(c -> new SetLineageFunction(c, Optional.empty(), Optional.of(system), rareOnly, rareWeight));
    }
}
