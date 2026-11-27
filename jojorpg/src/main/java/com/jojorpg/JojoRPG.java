package com.jojorpg;

import com.animecore.api.AnimeModContext;
import com.jojorpg.content.JojoItems;
import net.fabricmc.api.ModInitializer;

/**
 * JoJo's Bizarre Adventure, Parts 1-2 (Phantom Blood, Battle Tendency), on AnimeCore.
 * <ul>
 *   <li>Ripple (Hamon) - the primary energy, refilled by breathing (Hamon users breathe faster while still / crouching)</li>
 *   <li>Stamina - fists, kicks and weapons; Blood - what vampires and Pillar Men drink to fight</li>
 *   <li>Hamon users walk on water (Ripple surface adhesion) and dash with a burst of breath</li>
 * </ul>
 */
public final class JojoRPG implements ModInitializer {
    public static final String MOD_ID = "jojorpg";

    public static final AnimeModContext CTX = AnimeModContext.builder(MOD_ID)
            .primaryEnergy("ripple", 100f, 0.12f, 0xFFD54F)
            .secondaryEnergy("stamina", 100f, 0.4f, 0x8BC34A)
            .extraEnergy("blood", 100f, 0.02f, 0xB71C1C)
            .surfaceAdhesion("ripple", 0.04f, 2)          // the Ripple lets its users stand on water and cling to walls
            .burstStep("stamina", 8f, 6f, 25, 1)
            .passiveMovement()
            .overrideText(JojoText.ENGINE.keySet())       // "fighting style" instead of "lineage"
            .register();

    @Override
    public void onInitialize() {
        JojoItems.init();
    }
}
