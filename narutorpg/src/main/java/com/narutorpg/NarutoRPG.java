package com.narutorpg;

import com.animecore.api.AnimeModContext;
import com.narutorpg.content.NarutoItems;
import com.narutorpg.mechanic.NarutoMechanics;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

/** NarutoRPG: maps Naruto terminology onto the animecore engine. Content itself is 100% data-driven. */
public final class NarutoRPG implements ModInitializer {
    public static final String MOD_ID = "narutorpg";

    /** Chakra / Stamina pools, hand-sign animations, mobility, and the {@code narutorpg:power} CCA component. */
    public static final AnimeModContext CTX = AnimeModContext.builder(MOD_ID)
            .primaryEnergy("chakra", 100f, 0.15f, 0x3FA9F5)
            .secondaryEnergy("stamina", 100f, 0.4f, 0x8BC34A)
            .comboAnimations(sign -> Identifier.of(MOD_ID, "sign_" + sign.getPath()))
            .surfaceAdhesion("chakra", 0.05f, 1)          // Chakra control: tree & water walking
            .burstStep("chakra", 6f, 7f, 20, 1)            // Body Flicker (Shunshin): 7 blocks, 1s cooldown
            .register();

    @Override
    public void onInitialize() {
        NarutoItems.init();
        NarutoMechanics.register();
    }
}
