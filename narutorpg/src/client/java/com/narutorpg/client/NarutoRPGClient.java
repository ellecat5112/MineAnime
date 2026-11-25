package com.narutorpg.client;

import com.animecore.client.input.AnimeKeybinds;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.HandSigns;
import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.glfw.GLFW;

/**
 * Phase 1 controls (all under "NarutoRPG" in Controls): R/G/V quick-cast slots, B toggles Sharingan,
 * numpad 1-6 = Tiger, Ram, Snake, Bird, Boar, Horse, numpad 0 = release combo. Phase 2 adds the radial combo wheel.
 */
public final class NarutoRPGClient implements ClientModInitializer {
    private static final int[] SLOT_KEYS = {GLFW.GLFW_KEY_R, GLFW.GLFW_KEY_G, GLFW.GLFW_KEY_V};
    private static final int[] SIGN_KEYS = {GLFW.GLFW_KEY_KP_1, GLFW.GLFW_KEY_KP_2, GLFW.GLFW_KEY_KP_3,
            GLFW.GLFW_KEY_KP_4, GLFW.GLFW_KEY_KP_5, GLFW.GLFW_KEY_KP_6};

    @Override
    public void onInitializeClient() {
        for (int slot = 0; slot < SLOT_KEYS.length; slot++) AnimeKeybinds.hotbarSlot(NarutoRPG.CTX, slot, SLOT_KEYS[slot]);
        AnimeKeybinds.transform(NarutoRPG.CTX, GLFW.GLFW_KEY_B);
        for (int i = 0; i < HandSigns.ALL.size(); i++) AnimeKeybinds.comboToken(NarutoRPG.CTX, HandSigns.ALL.get(i), SIGN_KEYS[i]);
        AnimeKeybinds.comboCommit(NarutoRPG.CTX, GLFW.GLFW_KEY_KP_0);
    }
}
