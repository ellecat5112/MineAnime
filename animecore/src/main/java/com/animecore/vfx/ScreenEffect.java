package com.animecore.vfx;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

/**
 * Full-screen effect shown to players caught by a technique. {@link #AUTO} lets the engine decide from the ability's
 * style and effects (illusion / nausea -> {@link #ILLUSION}, heavy binding in shadow -> {@link #DARKNESS},
 * blinding light -> {@link #FLASH}).
 */
public enum ScreenEffect implements StringIdentifiable {
    AUTO, NONE, ILLUSION, DARKNESS, FLASH;

    public static final Codec<ScreenEffect> CODEC = StringIdentifiable.createCodec(ScreenEffect::values);

    @Override public String asString() { return name().toLowerCase(Locale.ROOT); }

    public static ScreenEffect byOrdinal(int i) {
        ScreenEffect[] v = values();
        return i >= 0 && i < v.length ? v[i] : NONE;
    }
}
