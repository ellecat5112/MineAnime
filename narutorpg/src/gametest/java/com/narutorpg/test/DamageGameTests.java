package com.narutorpg.test;

import com.animecore.ability.AbilityDamage;
import com.animecore.ability.action.SelfBuffAction;
import com.animecore.registry.AnimeRegistries;
import com.narutorpg.NarutoRPG;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Vec3d;

/** Offensive jutsu deal their full damage. */
@SuppressWarnings("removal")
public final class DamageGameTests implements FabricGameTest {

    /** A jutsu right after a melee hit, and every shot of a volley, deal full damage despite the hurt cooldown. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void jutsuIgnoresHurtCooldown(TestContext ctx) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        IronGolemEntity golem = ctx.spawnEntity(EntityType.IRON_GOLEM, 1, 2, 1);
        var world = ctx.getWorld();
        golem.damage(world.getDamageSources().playerAttack(p), 5f);   // melee hit starts the hurt cooldown
        float before = golem.getHealth();
        AbilityDamage.apply(world, p, p, golem, 4f, null, 0, 0f, p.getPos());
        ctx.assertTrue(before - golem.getHealth() >= 3.9f, "jutsu after a punch dealt " + (before - golem.getHealth()));
        before = golem.getHealth();
        for (int i = 0; i < 3; i++) AbilityDamage.apply(world, p, p, golem, 2f, null, 0, 0f, Vec3d.ZERO);
        ctx.assertTrue(before - golem.getHealth() >= 5.9f, "3-shot volley dealt " + (before - golem.getHealth()));
        ctx.complete();
    }

    /** Attacks whose wiki infobox lacks "Offensive" used to become harmless speed buffs. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void namedAttacksAreAttacks(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        for (String id : new String[]{"big_ball_rasenshuriken", "fire_release_big_flame_bullet", "earth_release_earth_spear",
                "crystal_release_crystal_pentagonal_prison", "truth_seeking_ball", "lightning_release_lightning_bullet"}) {
            var def = AnimeRegistries.ability(rm, NarutoRPG.CTX.id(id)).orElseThrow(() -> new AssertionError("missing " + id)).value();
            ctx.assertTrue(def.actions().stream().anyMatch(a -> !(a instanceof SelfBuffAction)), id + " is only a buff");
        }
        ctx.complete();
    }
}
