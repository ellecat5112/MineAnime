package com.animecore.registry;

import com.animecore.ability.action.AbilityActionType;
import com.animecore.platform.Ids;
import com.animecore.trait.TraitModifierType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * Static (code-defined) registries holding the polymorphic codec "types". Any mod may register additional
 * types under its own namespace, e.g. {@code narutorpg:shadow_clone} or {@code dragonballrpg:instant_transmission}.
 */
public final class AnimeBuiltinRegistries {
    private AnimeBuiltinRegistries() {}

    public static final RegistryKey<Registry<AbilityActionType<?>>> ABILITY_ACTION_TYPE_KEY =
            RegistryKey.ofRegistry(Ids.core("ability_action_type"));
    public static final RegistryKey<Registry<TraitModifierType<?>>> TRAIT_MODIFIER_TYPE_KEY =
            RegistryKey.ofRegistry(Ids.core("trait_modifier_type"));

    public static final Registry<AbilityActionType<?>> ABILITY_ACTION_TYPES =
            FabricRegistryBuilder.createSimple(ABILITY_ACTION_TYPE_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();
    public static final Registry<TraitModifierType<?>> TRAIT_MODIFIER_TYPES =
            FabricRegistryBuilder.createSimple(TRAIT_MODIFIER_TYPE_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static void init() {
        // class-load trigger
    }
}
