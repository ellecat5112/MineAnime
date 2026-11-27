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
            .passiveMovement()                             // no keybinds: water walking, look-up wall climbing, sneak+jump Shunshin
            .overrideText(NarutoText.ENGINE.keySet())       // jutsu / village / clan wording for engine messages
            .theme(new com.animecore.api.AnimeTheme(0xFF8F00, 0x29B6F6, null, "card.narutorpg.arc_complete", false))   // Konoha orange
            .register();

    @Override
    public void onInitialize() {
        NarutoItems.init();
        NarutoMechanics.register();
        // /naruto origin open|choose|random|info|set|reset - the engine's origin tree under the Naruto root.
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, access, env) ->
                dispatcher.register(net.minecraft.server.command.CommandManager.literal("naruto")
                        .then(com.animecore.command.OriginCommands.tree("origin", MOD_ID))));
        // /jutsu tokens: NarutoRPG's name for the engine's /techniques tokens
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, access, env) ->
                dispatcher.register(net.minecraft.server.command.CommandManager.literal("jutsu").then(net.minecraft.server.command.CommandManager
                        .literal("tokens").executes(c -> com.animecore.technique.TechniqueTokens.restore(c.getSource().getPlayerOrThrow(),
                                CTX.power(c.getSource().getPlayerOrThrow()))))));
    }
}
