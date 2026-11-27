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
    public static final AbilityActionType<AreaEffectAction> AREA_EFFECT = register(Ids.core("area_effect"), AreaEffectAction.CODEC);
    public static final AbilityActionType<RaiseWallAction> RAISE_WALL = register(Ids.core("raise_wall"), RaiseWallAction.CODEC);

    // manifestations (Stands) and other special powers
    public static final AbilityActionType<TimeStopAction> TIME_STOP = register(Ids.core("time_stop"), TimeStopAction.CODEC);
    public static final AbilityActionType<RushAction> RUSH = register(Ids.core("rush"), RushAction.CODEC);
    public static final AbilityActionType<RemoteStrikeAction> REMOTE_STRIKE = register(Ids.core("remote_strike"), RemoteStrikeAction.CODEC);
    public static final AbilityActionType<CharmAction> CHARM = register(Ids.core("charm"), CharmAction.CODEC);
    public static final AbilityActionType<DamageLinkAction> DAMAGE_LINK = register(Ids.core("damage_link"), DamageLinkAction.CODEC);
    public static final AbilityActionType<VoidAction> VOID = register(Ids.core("void"), VoidAction.CODEC);
    public static final AbilityActionType<AttributeShiftAction> ATTRIBUTE_SHIFT = register(Ids.core("attribute_shift"), AttributeShiftAction.CODEC);
    public static final AbilityActionType<ManifestAction> MANIFEST = register(Ids.core("manifest"), ManifestAction.CODEC);

    public static <T extends IAbilityAction> AbilityActionType<T> register(Identifier id, MapCodec<T> codec) {
        return Registry.register(AnimeBuiltinRegistries.ABILITY_ACTION_TYPES, id, new AbilityActionType<>(codec));
    }

    public static void init() {}
}
