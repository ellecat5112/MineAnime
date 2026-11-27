package com.jojorpg.client;

import com.animecore.client.input.AnimeKeybinds;
import com.animecore.movement.MovementMode;
import com.jojorpg.JojoRPG;
import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.glfw.GLFW;

/**
 * Item-based controls like NarutoRPG: technique items (Ripple, Vampirism, Pillar Men Arts...) cast on right-click,
 * sneak + scroll switches; the Modes item toggles Pillar Men modes. Optional keybinds exist but are unbound by default.
 */
public final class JojoRPGClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        var ctx = JojoRPG.CTX;
        for (int slot = 0; slot < 3; slot++) AnimeKeybinds.hotbarSlot(ctx, slot, GLFW.GLFW_KEY_UNKNOWN);
        AnimeKeybinds.transform(ctx, GLFW.GLFW_KEY_UNKNOWN);
        AnimeKeybinds.movementToggle(ctx, MovementMode.SURFACE_ADHESION, GLFW.GLFW_KEY_UNKNOWN);
        AnimeKeybinds.burstStep(ctx, GLFW.GLFW_KEY_UNKNOWN);
    }
}
