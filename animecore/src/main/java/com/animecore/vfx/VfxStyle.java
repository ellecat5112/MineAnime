package com.animecore.vfx;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Anime-agnostic elemental look of a technique. Every ability resolves to one style (explicit {@code visuals.style},
 * else its affinity's {@code vfx_style}, else a keyword guess from the affinity id, else {@link #ENERGY}), so any
 * technique added later - in any content mod - is animated without per-ability work. The client maps each style to
 * particles, palettes and procedural geometry (see {@code com.animecore.client.vfx}).
 *
 * <p>Palette: {@code core} (hottest / brightest), {@code mid}, {@code outer} (edge / smoke).
 */
public enum VfxStyle implements StringIdentifiable {
    ENERGY(0xFFFFFF, 0x9FD8FF, 0x3F7FFF),
    FLAME(0xFFF3B0, 0xFF8A1E, 0xC62800),
    WATER(0xE0F7FF, 0x40A8FF, 0x0D47A1),
    WIND(0xF4FFF4, 0xC8F7C5, 0x7FCF9F),
    LIGHTNING(0xFFFFFF, 0x9AD8FF, 0x4466FF),
    EARTH(0xD7B98E, 0x8D6E63, 0x4E342E),
    ICE(0xFFFFFF, 0xB3F0FF, 0x5EC8E8),
    PLANT(0xC5E1A5, 0x6D8B3A, 0x5D4037),
    LAVA(0xFFE082, 0xFF5722, 0x5D1A00),
    SAND(0xF3E2B3, 0xD7A86E, 0x8D6E3F),
    SHADOW(0x9C7BD1, 0x3B1E5C, 0x0A0A12),
    LIGHT(0xFFFFFF, 0xFFE57F, 0xFFB300),
    ILLUSION(0xFFD0F0, 0xC2185B, 0x4A148C),
    SPACE(0xE1BEE7, 0x7B1FA2, 0x12001F),
    POISON(0xDCEDC8, 0x7CB342, 0x33691E),
    BLOOD(0xFF8A80, 0xC62828, 0x4A0000),
    INSECT(0x9E9D24, 0x4E5B31, 0x1B1F10),
    SMOKE(0xEEEEEE, 0x9E9E9E, 0x424242);

    public static final Codec<VfxStyle> CODEC = StringIdentifiable.createCodec(VfxStyle::values);

    private final int core, mid, outer;

    VfxStyle(int core, int mid, int outer) {
        this.core = core;
        this.mid = mid;
        this.outer = outer;
    }

    public int core() { return core; }
    public int mid() { return mid; }
    public int outer() { return outer; }

    @Override public String asString() { return name().toLowerCase(Locale.ROOT); }

    public static VfxStyle byOrdinal(int i) {
        VfxStyle[] v = values();
        return i >= 0 && i < v.length ? v[i] : ENERGY;
    }

    /** Best-effort guess from an id path such as {@code fire}, {@code fire_release}, {@code kido_hado}. */
    public static @Nullable VfxStyle guess(String path) {
        String p = path.toLowerCase(Locale.ROOT);
        if (has(p, "fire", "flame", "blaze", "burn", "scorch", "heat", "katon")) return p.contains("lava") ? LAVA : FLAME;
        if (has(p, "lava", "magma", "volcan")) return LAVA;
        if (has(p, "water", "aqua", "boil", "tide", "ocean", "rain", "mist")) return WATER;
        if (has(p, "lightning", "thunder", "electric", "storm", "raiton", "spark")) return LIGHTNING;
        if (has(p, "wind", "air", "gale", "storm", "futon")) return WIND;
        if (has(p, "ice", "frost", "snow", "freez", "cold")) return ICE;
        if (has(p, "wood", "plant", "tree", "leaf", "flower", "nature")) return PLANT;
        if (has(p, "sand", "magnet", "iron_sand", "dust")) return SAND;
        if (has(p, "earth", "stone", "rock", "mud", "ground")) return EARTH;
        if (has(p, "yin", "shadow", "dark", "void", "hollow", "curse")) return SHADOW;
        if (has(p, "yang", "light", "holy", "sun", "divine")) return LIGHT;
        if (has(p, "illusion", "genjutsu", "mind", "dream")) return ILLUSION;
        if (has(p, "space", "time", "portal", "warp", "dimension")) return SPACE;
        if (has(p, "poison", "toxic", "venom", "acid")) return POISON;
        if (has(p, "blood")) return BLOOD;
        if (has(p, "insect", "bug", "bee", "swarm")) return INSECT;
        if (has(p, "smoke", "ash")) return SMOKE;
        return null;
    }

    private static boolean has(String p, String... keys) {
        for (String k : keys) if (p.contains(k)) return true;
        return false;
    }
}
