package com.animecore.loot;

import com.animecore.platform.Ids;
import com.mojang.serialization.MapCodec;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class AnimeLootFunctions {
    private AnimeLootFunctions() {}

    public static final LootFunctionType<SetAbilityScrollFunction> SET_ABILITY_SCROLL =
            register(Ids.core("set_ability_scroll"), SetAbilityScrollFunction.CODEC);
    public static final LootFunctionType<SetAnimeWeaponDataFunction> SET_ANIME_WEAPON_DATA =
            register(Ids.core("set_anime_weapon_data"), SetAnimeWeaponDataFunction.CODEC);
    public static final LootFunctionType<SetLineageFunction> SET_LINEAGE =
            register(Ids.core("set_lineage"), SetLineageFunction.CODEC);

    /** Content mods can register their own loot functions under their namespace with this helper. */
    public static <T extends LootFunction> LootFunctionType<T> register(Identifier id, MapCodec<T> codec) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, id, new LootFunctionType<>(codec));
    }

    public static void init() {}
}
