package com.narutorpg.test;

import com.animecore.ability.action.AreaEffectAction;
import com.animecore.ability.action.ProjectileAction;
import com.animecore.ability.action.SelfBuffAction;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.CastType;
import com.animecore.registry.definition.EnergyCost;
import com.animecore.vfx.AbilityVfx;
import com.animecore.vfx.ScreenEffect;
import com.animecore.vfx.VfxStyle;
import com.narutorpg.NarutoRPG;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

import static com.narutorpg.content.NarutoContent.CHAKRA;
import static com.narutorpg.content.NarutoContent.FIREBALL_JUTSU;

/** Technique VFX resolution: every existing and future ability gets a style, animation and (when apt) screen effect. */
public final class VfxGameTests implements FabricGameTest {

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void stylesResolveFromDataAndAffinity(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        AbilityDefinition fireball = AnimeRegistries.ability(rm, FIREBALL_JUTSU.getValue()).orElseThrow().value();
        ctx.assertTrue(AbilityVfx.resolveStyle(rm, fireball, FIREBALL_JUTSU.getValue()) == VfxStyle.FLAME, "fireball should be flame");
        ctx.assertTrue(AbilityVfx.defaultAnimation(fireball, VfxStyle.FLAME).getPath().equals("cast_exhale"), "fire projectile -> exhale");

        Identifier genjutsuId = NarutoRPG.CTX.id("genjutsu_sharingan");
        AbilityDefinition genjutsu = AnimeRegistries.ability(rm, genjutsuId).orElseThrow().value();
        VfxStyle gs = AbilityVfx.resolveStyle(rm, genjutsu, genjutsuId);
        ctx.assertTrue(gs == VfxStyle.ILLUSION, "genjutsu should be illusion, got " + gs);
        AreaEffectAction area = (AreaEffectAction) genjutsu.actions().get(0);
        ctx.assertTrue(AbilityVfx.resolveScreen(genjutsu, gs, area) == ScreenEffect.ILLUSION, "genjutsu shows the vision effect");

        // every Naruto ability resolves to a style and a cast animation
        for (var e : rm.get(AnimeRegistries.ABILITY).getEntrySet()) {
            if (!e.getValue().system().equals(NarutoRPG.MOD_ID)) continue;
            VfxStyle s = AbilityVfx.resolveStyle(rm, e.getValue(), e.getKey().getValue());
            ctx.assertTrue(s != null, "no style for " + e.getKey().getValue());
            ctx.assertTrue(e.getValue().castAnimation().isPresent() || AbilityVfx.defaultAnimation(e.getValue(), s) != null, "no animation");
        }
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void futureAbilitiesAreAnimatedWithoutVisualsData(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        // a technique a future content mod might add: no visuals block, water affinity, projectile
        AbilityDefinition water = new AbilityDefinition("narutorpg", Text.literal("Future Water Bullet"), CastType.INSTANT,
                new EnergyCost(CHAKRA, 10f, 0f), 20, 1, List.of(), 40, Optional.of(NarutoRPG.CTX.id("water")), Optional.empty(),
                Optional.empty(), List.of(new ProjectileAction(Optional.empty(), 1.4f, 6f, 0.6f, 0f, 0, 1, 0f, 40, 0xFFFFFF,
                Optional.empty(), false)));
        ctx.assertTrue(AbilityVfx.resolveStyle(rm, water, NarutoRPG.CTX.id("future_water_bullet")) == VfxStyle.WATER, "affinity style");
        // no affinity at all: keyword guess from the id, else generic energy
        AbilityDefinition plain = new AbilityDefinition("narutorpg", Text.literal("Mystery"), CastType.INSTANT,
                new EnergyCost(CHAKRA, 10f, 0f), 20, 1, List.of(), 40, Optional.empty(), Optional.empty(), Optional.empty(),
                List.of(new AreaEffectAction(6f, 360f, List.of(new SelfBuffAction.Effect(StatusEffects.NAUSEA, 60, 0, false)), 0f, 0f, 0)));
        ctx.assertTrue(AbilityVfx.resolveStyle(rm, plain, NarutoRPG.CTX.id("lightning_cage")) == VfxStyle.LIGHTNING, "keyword guess");
        VfxStyle energy = AbilityVfx.resolveStyle(rm, plain, NarutoRPG.CTX.id("mystery_art"));
        ctx.assertTrue(energy == VfxStyle.ENERGY, "fallback energy, got " + energy);
        ctx.assertTrue(AbilityVfx.resolveScreen(plain, energy, (AreaEffectAction) plain.actions().get(0)) == ScreenEffect.ILLUSION,
                "nausea-inflicting techniques show the illusion vision automatically");
        ctx.complete();
    }
}
