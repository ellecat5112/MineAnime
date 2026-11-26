package com.narutorpg.test;

import com.animecore.registry.AnimeRegistries;
import com.animecore.technique.TechniqueMastery;
import com.animecore.technique.TechniqueTokens;
import com.animecore.vfx.AbilityVfx;
import com.animecore.vfx.IllusionProfile;
import com.animecore.vfx.ScreenEffect;
import com.animecore.vfx.VfxStyle;
import com.narutorpg.NarutoRPG;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;

import java.util.HashSet;
import java.util.Set;

/** Fan genjutsu: every technique registered, on the Genjutsu token, with its own unique illusion profile. */
public final class GenjutsuGameTests implements FabricGameTest {
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void everyFanGenjutsuHasAUniqueVision(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        Set<String> signatures = new HashSet<>();
        Set<IllusionProfile.Pattern> patterns = new HashSet<>();
        int count = 0;
        for (var e : rm.get(AnimeRegistries.ABILITY).getEntrySet()) {
            if (!e.getKey().getValue().getPath().startsWith("fanon/")) continue;
            count++;
            var def = e.getValue();
            ctx.assertTrue(TechniqueTokens.tokenOf(def).equals(NarutoRPG.CTX.id("genjutsu")), e.getKey().getValue() + " not on the Genjutsu token");
            IllusionProfile p = def.visuals().illusion().orElse(null);
            ctx.assertTrue(p != null, e.getKey().getValue() + " has no illusion profile");
            ctx.assertTrue(signatures.add(p.pattern() + ":" + p.color() + ":" + p.color2() + ":" + p.count()), "duplicate vision " + e.getKey().getValue());
            patterns.add(p.pattern());
            var area = (com.animecore.ability.action.AreaEffectAction) def.actions().get(0);
            ctx.assertTrue(AbilityVfx.resolveScreen(def, VfxStyle.ILLUSION, area) == ScreenEffect.ILLUSION, "vision on hit");
        }
        ctx.assertTrue(count == 582, "582 fan genjutsu, got " + count);
        ctx.assertTrue(patterns.size() >= 18, "patterns used: " + patterns.size());
        ctx.assertTrue(TechniqueMastery.list(rm, NarutoRPG.CTX.id("genjutsu")).size() >= 582, "learnable through Genjutsu mastery");
        ctx.complete();
    }
}
