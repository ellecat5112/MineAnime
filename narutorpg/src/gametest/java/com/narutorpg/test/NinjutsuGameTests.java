package com.narutorpg.test;

import com.animecore.ability.AbilityExecutor;
import com.animecore.ability.CastResult;
import com.animecore.registry.AnimeRegistries;
import com.narutorpg.NarutoRPG;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

/** Narutopedia ninjutsu: every page is an ability and the utility mappings work. */
@SuppressWarnings("removal")
public final class NinjutsuGameTests implements FabricGameTest {

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void allNinjutsuRegistered(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        int total = rm.get(AnimeRegistries.ABILITY).size();
        ctx.assertTrue(total >= 2890, "only " + total + " abilities");
        for (String id : new String[]{"rasengan", "chidori", "shadow_clone_technique", "flying_thunder_god_technique", "sensing_technique",
                "hiding_in_shadow_technique", "spider_web_area", "chakra_scalpel"}) {
            ctx.assertTrue(AnimeRegistries.ability(rm, NarutoRPG.CTX.id(id)).isPresent(), "missing ninjutsu " + id);
        }
        ctx.complete();
    }

    /** Sensing reveals nearby enemies; hiding makes the user invisible. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void sensingAndHiding(TestContext ctx) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(new BlockPos(1, 2, 1)));
        p.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        var power = NarutoRPG.CTX.power(p);
        power.setTier(10);
        LivingEntity zombie = ctx.spawnEntity(EntityType.ZOMBIE, 1, 2, 6);
        for (String id : new String[]{"sensing_technique", "hiding_in_shadow_technique"}) {
            power.unlockAbility(NarutoRPG.CTX.id(id));
            var entry = AnimeRegistries.ability(p.getRegistryManager(), NarutoRPG.CTX.id(id)).orElseThrow();
            ctx.assertTrue(AbilityExecutor.cast(p, entry, power, 0f, null) == CastResult.SUCCESS, id + " cast failed");
        }
        ctx.assertTrue(zombie.hasStatusEffect(StatusEffects.GLOWING), "sensing did not reveal the zombie");
        ctx.assertTrue(p.hasStatusEffect(StatusEffects.INVISIBILITY), "hiding did not hide the user");
        ctx.complete();
    }

    private static LivingEntity range(TestContext ctx, ServerPlayerEntity p, String ability) {
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(new BlockPos(1, 2, 0)));
        p.changeGameMode(GameMode.SURVIVAL);
        p.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        var power = NarutoRPG.CTX.power(p);
        power.setTier(10);
        power.unlockAbility(NarutoRPG.CTX.id(ability));
        LivingEntity golem = ctx.spawnEntity(EntityType.IRON_GOLEM, 1, 2, 5);
        ((net.minecraft.entity.mob.MobEntity) golem).setAiDisabled(true);
        var entry = AnimeRegistries.ability(p.getRegistryManager(), NarutoRPG.CTX.id(ability)).orElseThrow();
        ctx.assertTrue(AbilityExecutor.cast(p, entry, power, 0f, null) == CastResult.SUCCESS, ability + " cast failed");
        return golem;
    }

    /** Teleport strikes hit what they pass through. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void teleportStrikeHits(TestContext ctx) {
        LivingEntity golem = range(ctx, ctx.createMockCreativeServerPlayerInWorld(), "flying_thunder_god_technique");
        ctx.assertTrue(golem.getHealth() < 100f, "Flying Thunder God missed the golem");
        ctx.complete();
    }

    /** An exploding projectile damages a large target it hits directly. */
    @GameTest(templateName = EMPTY_STRUCTURE, tickLimit = 80)
    public void explodingShotHitsLargeTarget(TestContext ctx) {
        LivingEntity golem = range(ctx, ctx.createMockCreativeServerPlayerInWorld(), "air_raid_shot");
        ctx.waitAndRun(30, () -> {
            ctx.assertTrue(golem.getHealth() < 100f, "Air Raid Shot exploded on the golem without damaging it");
            ctx.complete();
        });
    }

    /** Dashes vault a one-block step: a target standing on a platform is still reached and struck. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void dashClimbsSteps(TestContext ctx) {
        for (int x = 0; x < 3; x++) for (int z = 3; z < 8; z++) ctx.setBlockState(x, 2, z, net.minecraft.block.Blocks.STONE);
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(new BlockPos(1, 2, 0)));
        p.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);
        var power = NarutoRPG.CTX.power(p);
        power.setTier(10);
        power.unlockAbility(NarutoRPG.CTX.id("flying_thunder_god_technique"));
        LivingEntity golem = ctx.spawnEntity(EntityType.IRON_GOLEM, 1, 3, 5);
        ((net.minecraft.entity.mob.MobEntity) golem).setAiDisabled(true);
        var entry = AnimeRegistries.ability(p.getRegistryManager(), NarutoRPG.CTX.id("flying_thunder_god_technique")).orElseThrow();
        ctx.assertTrue(AbilityExecutor.cast(p, entry, power, 0f, null) == CastResult.SUCCESS, "cast failed");
        ctx.assertTrue(golem.getHealth() < 100f, "the dash stopped at the step instead of reaching the golem");
        ctx.complete();
    }
}
