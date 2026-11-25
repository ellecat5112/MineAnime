package com.animecore.client.hud;

/**
 * Screen corners reserved for anime HUDs. The right side is deliberately never used: Essential's social UI and
 * vanilla status effects live there.
 */
public enum HudAnchor {
    /** Stacks downward from the top-left corner (energy bars, transformation). */
    TOP_LEFT,
    /** Stacks upward from the bottom-left corner (ability slots, combo trail). Hidden while chat is open. */
    BOTTOM_LEFT
}
