package com.animecore.ability.action;

import com.animecore.platform.Ids;
import com.animecore.registry.AnimeBuiltinRegistries;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Built-in, anime-agnostic action types. Content mods register more with {@link #register(Identifier, MapCodec)}
 * using their own namespace.
 */
public final class AbilityActions {
    private AbilityActions() {}

    public static final AbilityActionType<ProjectileAction> PROJECTILE = register(Ids.core("projectile"), ProjectileAction.CODEC);
    public static final AbilityActionType<AoeSphereAction> AOE_SPHERE = register(Ids.core("aoe_sphere"), AoeSphereAction.CODEC);
    public static final AbilityActionType<BeamRaycastAction> BEAM_RAYCAST = register(Ids.core("beam_raycast"), BeamRaycastAction.CODEC);
    public static final AbilityActionType<DashStrikeAction> DASH_STRIKE = register(Ids.core("dash_strike"), DashStrikeAction.CODEC);
    public static final AbilityActionType<SelfBuffAction> SELF_BUFF = register(Ids.core("self_buff"), SelfBuffAction.CODEC);
    public static final AbilityActionType<SummonEntityAction> SUMMON_ENTITY = register(Ids.core("summon_entity"), SummonEntityAction.CODEC);

    public static <T extends IAbilityAction> AbilityActionType<T> register(Identifier id, MapCodec<T> codec) {
        return Registry.register(AnimeBuiltinRegistries.ABILITY_ACTION_TYPES, id, new AbilityActionType<>(codec));
    }

    public static void init() {}
}
