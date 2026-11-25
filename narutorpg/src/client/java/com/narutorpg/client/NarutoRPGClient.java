package com.narutorpg.client;

import com.animecore.client.input.AnimeKeybinds;
import com.animecore.movement.MovementMode;
import com.narutorpg.NarutoRPG;
import com.narutorpg.content.HandSigns;
import com.animecore.client.render.HeldModels;
import com.narutorpg.content.NarutoTools;
import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.glfw.GLFW;

/**
 * Controls (all under "NarutoRPG" in Controls; everything rebindable):
 * <ul>
 *   <li>R / G / V - jutsu slots 1-3 (hold to charge)</li>
 *   <li>Z (hold) - hand-sign wheel; numpad 1-6 + numpad 0 as a keyboard alternative</li>
 *   <li>B - toggle Sharingan, N / M - next / previous stage</li>
 *   <li>J - chakra control (tree &amp; water walking), Left Alt - Body Flicker (Shunshin)</li>
 * </ul>
 */
public final class NarutoRPGClient implements ClientModInitializer {
    private static final int[] SLOT_KEYS = {GLFW.GLFW_KEY_R, GLFW.GLFW_KEY_G, GLFW.GLFW_KEY_V};
    private static final int[] SIGN_KEYS = {GLFW.GLFW_KEY_KP_1, GLFW.GLFW_KEY_KP_2, GLFW.GLFW_KEY_KP_3,
            GLFW.GLFW_KEY_KP_4, GLFW.GLFW_KEY_KP_5, GLFW.GLFW_KEY_KP_6};

    @Override
    public void onInitializeClient() {
        var ctx = NarutoRPG.CTX;
        for (int slot = 0; slot < SLOT_KEYS.length; slot++) AnimeKeybinds.hotbarSlot(ctx, slot, SLOT_KEYS[slot]);
        AnimeKeybinds.comboWheel(ctx, HandSigns.ALL, GLFW.GLFW_KEY_Z);
        for (int i = 0; i < HandSigns.ALL.size(); i++) AnimeKeybinds.comboToken(ctx, HandSigns.ALL.get(i), SIGN_KEYS[i]);
        AnimeKeybinds.comboCommit(ctx, GLFW.GLFW_KEY_KP_0);
        AnimeKeybinds.transform(ctx, GLFW.GLFW_KEY_B);
        AnimeKeybinds.transformStage(ctx, +1, GLFW.GLFW_KEY_N);
        AnimeKeybinds.transformStage(ctx, -1, GLFW.GLFW_KEY_M);
        AnimeKeybinds.movementToggle(ctx, MovementMode.SURFACE_ADHESION, GLFW.GLFW_KEY_J);
        AnimeKeybinds.burstStep(ctx, GLFW.GLFW_KEY_LEFT_ALT);

        // Low-poly 3D models for weapons (tools/catalog/model3d.py): flat icon in GUIs, 3D in hand / world.
        NarutoTools.model3dIds().forEach(id -> HeldModels.register(NarutoTools.item(id), ctx.id("item/3d/" + id)));
    }
}
