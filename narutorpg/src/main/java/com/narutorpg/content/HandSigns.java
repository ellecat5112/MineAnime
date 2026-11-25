package com.narutorpg.content;

import com.narutorpg.NarutoRPG;
import net.minecraft.util.Identifier;

import java.util.List;

/** Combo tokens. Each maps to the PlayerAnimator animation {@code narutorpg:sign_<name>}. */
public final class HandSigns {
    private HandSigns() {}

    public static final Identifier TIGER = id("tiger");
    public static final Identifier RAM = id("ram");
    public static final Identifier SNAKE = id("snake");
    public static final Identifier BIRD = id("bird");
    public static final Identifier BOAR = id("boar");
    public static final Identifier HORSE = id("horse");

    /** Order used by keybinds (and by the Phase 2 combo wheel). */
    public static final List<Identifier> ALL = List.of(TIGER, RAM, SNAKE, BIRD, BOAR, HORSE);

    private static Identifier id(String path) {
        return Identifier.of(NarutoRPG.MOD_ID, path);
    }
}
