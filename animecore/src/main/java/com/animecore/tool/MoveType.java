package com.animecore.tool;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

/** Reusable tool moves. Each is implemented once in {@link ToolMoves} and parameterised per tool. */
public enum MoveType implements StringIdentifiable {
    /** Throw the item (or a copy) as a projectile: kunai, shuriken, senbon, bombs, tags. */
    THROW(false),
    /** Scatter ground traps: makibishi. */
    TRAP(false),
    /** Place a barrier tag on the clicked block (see the ward system). */
    WARD(false),
    /** Eat / drink: pills, medicine, perfume, curry. */
    CONSUME(true),
    /** Hold and release a charged energy slash. */
    CHARGE_SLASH(true),
    /** Lunge forward striking everything on the path. */
    DASH(false),
    /** Fire a chain / wire / rope hook: pulls entities in, or grapples you to blocks. */
    CHAIN(false),
    /** Hold to block attacks from the front (optionally reflecting projectiles or absorbing chakra). */
    GUARD(true),
    /** Cone of wind: fans, umbrellas. */
    GUST(false),
    /** Elemental / chakra blast: projectile, beam, cone or lightning strike. */
    BLAST(false),
    /** Seal a weakened target, or bind a healthy one. */
    SEAL(false),
    /** Summon puppet / drone / toad allies. */
    SUMMON(false),
    /** Sand / iron sand / gold dust wave. */
    SAND(false),
    /** Flying Thunder God: throw a marker, then warp to it. */
    MARK(false),
    /** Warp to the marker thrown with MARK. */
    RECALL(false),
    /** Save a gate anchor at your position / warp back to it. */
    ANCHOR(false),
    /** Space–time vortex that drags everything toward a point. */
    VORTEX(false),
    /** Launch / glide: wings, flying ships, snowboards. */
    GLIDE(false),
    /** Sound wave: flutes, bells, echo speakers, drums. */
    SONIC(false),
    /** Reveal nearby entities and read the target: bingo book, scopes, drones, glasses. */
    SCAN(false),
    /** Self buff for chakra. */
    BUFF(false),
    /** Raise a temporary wall in front of you. */
    WALL(false),
    /** Ground slam / explosive impact / drilling. */
    SLAM(false),
    /** Toggle elemental infusion on the item (chakra blades). */
    INFUSE(false);

    public static final Codec<MoveType> CODEC = StringIdentifiable.createCodec(MoveType::values);
    private final boolean held;

    MoveType(boolean held) { this.held = held; }

    /** Held moves start "using" the item (eat animation, guard pose, charge). */
    public boolean held() { return held; }

    @Override public String asString() { return name().toLowerCase(java.util.Locale.ROOT); }
}
