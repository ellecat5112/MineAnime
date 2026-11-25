package com.animecore.loot;

import com.animecore.item.AnimeDataComponents;
import com.animecore.registry.AnimeRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.List;
import java.util.Optional;

/**
 * {@code "function": "animecore:set_anime_weapon_data"}. Writes the {@code animecore:weapon_infusion} component:
 * a fixed {@code affinity}, or a random base (non-fused) affinity of {@code random_affinity_from}.
 */
public final class SetAnimeWeaponDataFunction extends ConditionalLootFunction {
    public static final MapCodec<SetAnimeWeaponDataFunction> CODEC = RecordCodecBuilder.mapCodec(i -> addConditionsField(i).and(i.group(
            Identifier.CODEC.optionalFieldOf("affinity").forGetter(f -> f.affinity),
            Codec.STRING.optionalFieldOf("random_affinity_from").forGetter(f -> f.randomFrom),
            Codec.BOOL.optionalFieldOf("active", false).forGetter(f -> f.active)
    )).apply(i, SetAnimeWeaponDataFunction::new));

    private final Optional<Identifier> affinity;
    private final Optional<String> randomFrom;
    private final boolean active;

    private SetAnimeWeaponDataFunction(List<LootCondition> conditions, Optional<Identifier> affinity, Optional<String> randomFrom, boolean active) {
        super(conditions);
        this.affinity = affinity;
        this.randomFrom = randomFrom;
        this.active = active;
    }

    @Override
    public LootFunctionType<SetAnimeWeaponDataFunction> getType() {
        return AnimeLootFunctions.SET_ANIME_WEAPON_DATA;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        Optional<Identifier> chosen = affinity.or(() -> randomFrom.flatMap(system -> {
            List<Identifier> pool = context.getWorld().getRegistryManager().get(AnimeRegistries.AFFINITY).getEntrySet().stream()
                    .filter(e -> e.getValue().system().equals(system) && e.getValue().fusionOf().isEmpty())
                    .map(e -> e.getKey().getValue()).sorted().toList();
            return pool.isEmpty() ? Optional.empty() : Optional.of(Util.getRandom(pool, context.getRandom()));
        }));
        stack.set(AnimeDataComponents.WEAPON_INFUSION, new AnimeDataComponents.WeaponInfusion(chosen, active));
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> fixed(Identifier affinity, boolean active) {
        return builder(conditions -> new SetAnimeWeaponDataFunction(conditions, Optional.of(affinity), Optional.empty(), active));
    }

    public static ConditionalLootFunction.Builder<?> randomAffinity(String system) {
        return builder(conditions -> new SetAnimeWeaponDataFunction(conditions, Optional.empty(), Optional.of(system), false));
    }
}
