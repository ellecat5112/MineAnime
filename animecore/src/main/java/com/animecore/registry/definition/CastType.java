package com.animecore.registry.definition;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum CastType implements StringIdentifiable {
    /** Fires immediately on press / combo completion. */
    INSTANT("instant"),
    /** Hold to charge (Rasengan, Kamehameha, Cero); power scales with charge ratio, released on key-up. */
    CHARGED("charged"),
    /** Arms a short window; triggers automatically when the player is hit (Substitution, Z-Vanish, Flash Step dodge). */
    REACTIVE("reactive");

    public static final Codec<CastType> CODEC = StringIdentifiable.createCodec(CastType::values);
    private final String name;

    CastType(String name) { this.name = name; }

    @Override public String asString() { return name; }
}
