package com.animecore.movement;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

/** Anime mobility archetypes shared by every power system. */
public enum MovementMode implements StringIdentifiable {
    /** Tree / wall / water walking (Naruto chakra control, JJK cursed-energy footing). Toggle, drains while in use. */
    SURFACE_ADHESION("surface_adhesion", true),
    /** Free 3D flight (Dragon Ball Bukujutsu, Bleach air-standing, One Piece Geppo spam). Toggle, drains while flying. */
    OMNIDIRECTIONAL_FLIGHT("omnidirectional_flight", true),
    /** Instant short-range blink with afterimages (Shunshin, Flash Step, Z-Dash, Soru). One-shot with cooldown. */
    BURST_STEP("burst_step", false);

    public static final Codec<MovementMode> CODEC = StringIdentifiable.createCodec(MovementMode::values);
    private final String name;
    private final boolean continuous;

    MovementMode(String name, boolean continuous) {
        this.name = name;
        this.continuous = continuous;
    }

    public boolean continuous() { return continuous; }

    public String translationKey() { return "movement.animecore." + name; }

    @Override public String asString() { return name; }
}
