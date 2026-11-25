package com.animecore.client.hud;

/** Pure layout math (relative to screen size) so it can be reasoned about and tested without a client. */
public final class HudLayout {
    private HudLayout() {}

    public static final int ELEMENT_GAP = 2;
    public static final int GROUP_GAP = 5;

    /** Corner margin: 1% of the width + 2px, so it scales with GUI scale and ultrawide screens. */
    public static int margin(int screenWidth) {
        return Math.round(screenWidth * 0.01f) + 2;
    }

    /** Panel width: 22% of the screen, clamped so text stays legible and the vanilla hotbar is never reached. */
    public static int panelWidth(int screenWidth) {
        int maxBeforeHotbar = screenWidth / 2 - 91 - 2 * margin(screenWidth);
        return Math.max(60, Math.min(Math.min(170, Math.round(screenWidth * 0.22f)), Math.max(60, maxBeforeHotbar)));
    }
}
