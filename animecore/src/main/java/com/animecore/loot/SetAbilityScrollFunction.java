package com.animecore.loot;

import com.animecore.item.AnimeDataComponents;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.List;
import java.util.Optional;

/**
 * {@code "function": "animecore:set_ability_scroll"}. Writes the 1.21.1 {@code animecore:ability_scroll} component:
 * either a fixed {@code ability}, or a random one from {@code system} up to {@code max_tier}.
 * <p>
 * The random pick uses {@link LootContext#getRandom()}, so Lootr's per-player chest rolls each produce their own
 * scroll; no Lootr-specific code is needed.
 */
public final class SetAbilityScrollFunction extends ConditionalLootFunction {
    public static final MapCodec<SetAbilityScrollFunction> CODEC = RecordCodecBuilder.mapCodec(i -> addConditionsField(i).and(i.group(
            Identifier.CODEC.optionalFieldOf("ability").forGetter(f -> f.ability),
            Codec.STRING.optionalFieldOf("system").forGetter(f -> f.system),
            Codec.intRange(1, 1000).optionalFieldOf("max_tier", 1000).forGetter(f -> f.maxTier)
    )).apply(i, SetAbilityScrollFunction::new));

    private final Optional<Identifier> ability;
    private final Optional<String> system;
    private final int maxTier;

    private SetAbilityScrollFunction(List<LootCondition> conditions, Optional<Identifier> ability, Optional<String> system, int maxTier) {
        super(conditions);
        this.ability = ability;
        this.system = system;
        this.maxTier = maxTier;
    }

    @Override
    public LootFunctionType<SetAbilityScrollFunction> getType() {
        return AnimeLootFunctions.SET_ABILITY_SCROLL;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        Optional<Identifier> chosen = ability.or(() -> {
            Registry<AbilityDefinition> registry = context.getWorld().getRegistryManager().get(AnimeRegistries.ABILITY);
            List<Identifier> pool = registry.getEntrySet().stream()
                    .filter(e -> system.map(e.getValue().system()::equals).orElse(true) && e.getValue().requiredTier() <= maxTier)
                    .map(e -> e.getKey().getValue()).sorted().toList();
            return pool.isEmpty() ? Optional.empty() : Optional.of(Util.getRandom(pool, context.getRandom()));
        });
        chosen.ifPresent(id -> stack.set(AnimeDataComponents.ABILITY_SCROLL, new AnimeDataComponents.AbilityScroll(id)));
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> fixed(Identifier ability) {
        return builder(conditions -> new SetAbilityScrollFunction(conditions, Optional.of(ability), Optional.empty(), 1000));
    }

    public static ConditionalLootFunction.Builder<?> random(String system, int maxTier) {
        return builder(conditions -> new SetAbilityScrollFunction(conditions, Optional.empty(), Optional.of(system), maxTier));
    }
}
