package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Battle cries ("ORA ORA ORA!", "ZA WARUDO!") on the action bar of everyone within 32 blocks of the caster. */
final class Shouts {
    private Shouts() {}

    static void shout(AbilityContext ctx, Text text) {
        Text line = text.copy().formatted(Formatting.BOLD, Formatting.GOLD);
        for (ServerPlayerEntity p : ctx.world().getPlayers()) {
            if (p.squaredDistanceTo(ctx.caster()) < 32 * 32) p.sendMessage(line, true);
        }
    }
}
