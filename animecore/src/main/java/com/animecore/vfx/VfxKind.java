package com.animecore.vfx;

/** Shape of a one-shot technique effect sent to clients ({@link VfxPayload}). */
public enum VfxKind {
    /** Energy gathering into the caster's hands at release. size = power. */
    CAST,
    /** Ribbon from origin along dir for {@code size} blocks; extra = width. */
    BEAM,
    /** Expanding ring(s) at origin; size = radius. */
    SHOCKWAVE,
    /** Sphere / cone field; size = radius, extra = cone degrees (360 = sphere). */
    AREA,
    /** Ground eruption along dir (wall axis); size = width, extra = height. */
    WALL,
    /** Aura column around the caster; size = intensity. */
    BUFF,
    /** Afterimage streak from origin along dir for size blocks. */
    DASH,
    /** Burst at a projectile / strike hit; size = radius. */
    IMPACT,
    /** Lingering mark above a target (illusion spiral, binding chains); size = duration ticks. */
    MARK,
    /** Genjutsu field around the cast with its own pattern: dir = (pattern ordinal, count, speed), size = radius, extra = color2. */
    ILLUSION;

    public static VfxKind byOrdinal(int i) {
        VfxKind[] v = values();
        return i >= 0 && i < v.length ? v[i] : CAST;
    }
}
