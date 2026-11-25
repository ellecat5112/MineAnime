package com.animecore.animation;

import com.animecore.platform.Ids;
import net.minecraft.util.Identifier;

/**
 * PlayerAnimator priority layers shared by all anime mods (a player can only perform one hand sign at a time,
 * whether it comes from Naruto or Jujutsu Kaisen). Higher priority renders on top.
 */
public enum AnimLayer {
    /** Full-body stances: transformation idle, flight pose, wall-walk lean. */
    BASE(1000, "base"),
    /** Upper-body overrides: hand signs, charge poses, beam hold. Legs keep vanilla walk cycle. */
    UPPER_BODY(1500, "upper_body"),
    /** One-shot reactions on top of everything: substitution poof, hit flinch, finisher. */
    OVERLAY(2000, "overlay");

    private final int priority;
    private final Identifier id;

    AnimLayer(int priority, String path) {
        this.priority = priority;
        this.id = Ids.core("layer/" + path);
    }

    public int priority() { return priority; }
    public Identifier id() { return id; }
}
