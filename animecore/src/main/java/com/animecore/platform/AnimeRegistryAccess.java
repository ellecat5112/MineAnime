package com.animecore.platform;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * Version shield for registry lookups. 1.21.2+ renamed {@code getWrapperOrThrow -> getOrThrow},
 * {@code DynamicRegistryManager#get -> getOrThrow}, {@code Registry#getEntry(Identifier) -> getEntry(Identifier)}
 * signatures etc. Engine code never calls those methods directly.
 */
public final class AnimeRegistryAccess {
    private AnimeRegistryAccess() {}

    public static <T> Registry<T> registry(DynamicRegistryManager manager, RegistryKey<? extends Registry<? extends T>> key) {
        return manager.get(key);
    }

    public static <T> Registry<T> registry(World world, RegistryKey<? extends Registry<? extends T>> key) {
        return registry(world.getRegistryManager(), key);
    }

    public static <T> Optional<RegistryEntry.Reference<T>> entry(DynamicRegistryManager manager, RegistryKey<? extends Registry<? extends T>> key, Identifier id) {
        Registry<T> reg = registry(manager, key);
        return reg.getEntry(id);
    }

    public static <T> Optional<T> value(DynamicRegistryManager manager, RegistryKey<? extends Registry<? extends T>> key, Identifier id) {
        Registry<T> reg = registry(manager, key);
        return reg.getOrEmpty(id);
    }

    /** Wrapper lookup used by data generators and NBT serialization. */
    public static <T> RegistryWrapper.Impl<T> wrapper(RegistryWrapper.WrapperLookup lookup, RegistryKey<? extends Registry<? extends T>> key) {
        return lookup.getWrapperOrThrow(key);
    }
}
