package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.manifestation.ManifestationService;
import com.mojang.serialization.MapCodec;

/** Calls the caster's manifestation (Stand) out, or back if it is already out. */
public record ManifestAction() implements IAbilityAction {
    public static final MapCodec<ManifestAction> CODEC = MapCodec.unit(new ManifestAction());

    @Override public AbilityActionType<?> type() { return AbilityActions.MANIFEST; }

    @Override
    public void execute(AbilityContext ctx) {
        if (ctx.player() != null && ctx.power() != null) {
            // the pre-cast hook already called it out if it was away; a second use sends it back
            ManifestationService.setManifested(ctx.player(), ctx.power(), !ctx.power().manifested() || !wasOut(ctx));
        }
    }

    private static boolean wasOut(AbilityContext ctx) {
        var stand = ManifestationService.standOf(ctx.caster());
        return stand != null && stand.age > 2;
    }
}
