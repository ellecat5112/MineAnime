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
 * Controls are item-based (AHZNB-style) - no keybinds needed:
 * <ul>
 *   <li>jutsu tokens (Fire Release, Ninjutsu, Dōjutsu...): right-click to cast, hold to charge, sneak + scroll / sneak +
 *       left-click to switch jutsu; the Dōjutsu token toggles the eyes</li>
 *   <li>movement: water walking is automatic, jump at a wall while looking up to climb, sneak + jump for Body Flicker</li>
 * </ul>
 * The old keybinds still exist under Controls › NarutoRPG but are <b>unbound by default</b> for players who prefer them.
 */
public final class NarutoRPGClient implements ClientModInitializer {
    private static final int UNBOUND = GLFW.GLFW_KEY_UNKNOWN;
    private static final int[] SLOT_KEYS = {UNBOUND, UNBOUND, UNBOUND};
    private static final int[] SIGN_KEYS = {UNBOUND, UNBOUND, UNBOUND, UNBOUND, UNBOUND, UNBOUND};

    @Override
    public void onInitializeClient() {
        var ctx = NarutoRPG.CTX;
        for (int slot = 0; slot < SLOT_KEYS.length; slot++) AnimeKeybinds.hotbarSlot(ctx, slot, SLOT_KEYS[slot]);
        AnimeKeybinds.comboWheel(ctx, HandSigns.ALL, UNBOUND);
        for (int i = 0; i < HandSigns.ALL.size(); i++) AnimeKeybinds.comboToken(ctx, HandSigns.ALL.get(i), SIGN_KEYS[i]);
        AnimeKeybinds.comboCommit(ctx, UNBOUND);
        AnimeKeybinds.transform(ctx, UNBOUND);
        AnimeKeybinds.transformStage(ctx, +1, UNBOUND);
        AnimeKeybinds.transformStage(ctx, -1, UNBOUND);
        AnimeKeybinds.movementToggle(ctx, MovementMode.SURFACE_ADHESION, UNBOUND);
        AnimeKeybinds.burstStep(ctx, UNBOUND);

        // Low-poly 3D models for weapons (tools/catalog/model3d.py): flat icon in GUIs, 3D in hand / world.
        NarutoTools.model3dIds().forEach(id -> HeldModels.register(NarutoTools.item(id), ctx.id("item/3d/" + id)));
    }
}
