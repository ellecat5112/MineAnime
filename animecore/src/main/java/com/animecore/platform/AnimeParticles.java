package com.animecore.platform;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

/**
 * Version shield for particle construction ({@link DustParticleEffect} switched from a {@code Vector3f} to a packed
 * int color in 1.21.2). Tintable dust is the backbone of the texture-free procedural aura / energy visuals.
 */
public final class AnimeParticles {
    private AnimeParticles() {}

    public static DustParticleEffect dust(int rgb, float scale) {
        return new DustParticleEffect(rgbToVec(rgb), Math.max(0.01f, Math.min(4f, scale)));
    }

    public static Vector3f rgbToVec(int rgb) {
        return new Vector3f(((rgb >> 16) & 0xFF) / 255f, ((rgb >> 8) & 0xFF) / 255f, (rgb & 0xFF) / 255f);
    }

    /** Resolves a simple (parameterless) particle type such as {@code minecraft:flame}; null if unknown/complex. */
    public static @Nullable ParticleEffect simple(@Nullable Identifier id) {
        if (id == null) return null;
        return Registries.PARTICLE_TYPE.get(id) instanceof SimpleParticleType simple ? simple : null;
    }
}
