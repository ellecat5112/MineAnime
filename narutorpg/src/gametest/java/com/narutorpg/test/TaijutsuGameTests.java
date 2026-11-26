package com.narutorpg.test;

import com.animecore.ability.AbilityExecutor;
import com.animecore.ability.CastResult;
import com.animecore.registry.AnimeRegistries;
import com.animecore.technique.TechniqueMastery;
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

/** Narutopedia taijutsu: every page is an ability, and the move archetypes behave as designed. */
@SuppressWarnings("removal")
public final class TaijutsuGameTests implements FabricGameTest {

    private static ServerPlayerEntity fighter(TestContext ctx, String... abilities) {
        ServerPlayerEntity p = ctx.createMockCreativeServerPlayerInWorld();
        p.changeGameMode(GameMode.SURVIVAL);
        Vec3d at = Vec3d.ofBottomCenter(ctx.getAbsolutePos(new BlockPos(1, 2, 1)));
        p.refreshPositionAndAngles(at.x, at.y, at.z, 0f, 0f);   // facing +Z
        var power = NarutoRPG.CTX.power(p);
        power.setTier(10);
        for (String a : abilities) power.unlockAbility(NarutoRPG.CTX.id(a));
        return p;
    }

    private static CastResult cast(ServerPlayerEntity p, String id) {
        var entry = AnimeRegistries.ability(p.getRegistryManager(), NarutoRPG.CTX.id(id)).orElseThrow(() -> new AssertionError("missing " + id));
        return AbilityExecutor.cast(p, entry, NarutoRPG.CTX.power(p), 0f, null);
    }

    private static LivingEntity dummy(TestContext ctx, EntityType<? extends LivingEntity> type) {
        LivingEntity e = ctx.spawnEntity(type, 1, 2, 3);
        if (e instanceof net.minecraft.entity.mob.MobEntity m) m.setAiDisabled(true);
        return e;
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void allTaijutsuRegistered(TestContext ctx) {
        var rm = ctx.getWorld().getRegistryManager();
        for (String id : new String[]{"morning_peacock", "daytime_tiger", "evening_elephant", "night_guy", "eight_gates_released_formation",
                "multiple_connected_fists", "leaf_rock_destroying_rise", "wild_boar_spin", "windmill_throw", "100_single_punch"}) {
            ctx.assertTrue(AnimeRegistries.ability(rm, NarutoRPG.CTX.id(id)).isPresent(), "missing taijutsu " + id);
        }
        int onToken = TechniqueMastery.list(rm, NarutoRPG.CTX.id("taijutsu")).size();
        ctx.assertTrue(onToken >= 400, "taijutsu mastery list has " + onToken);
        ctx.complete();
    }

    /** Barrage: the first blow lands at once, the follow-ups land over the next ticks. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void barrageFollowsUp(TestContext ctx) {
        ServerPlayerEntity p = fighter(ctx, "multiple_connected_fists");
        LivingEntity golem = dummy(ctx, EntityType.IRON_GOLEM);
        float start = golem.getHealth();
        ctx.assertTrue(cast(p, "multiple_connected_fists") == CastResult.SUCCESS, "cast failed");
        float afterFirst = golem.getHealth();
        ctx.assertTrue(afterFirst < start, "first blow missed");
        ctx.waitAndRun(20, () -> {
            float total = start - golem.getHealth();
            ctx.assertTrue(total > (start - afterFirst) * 4, "follow-up blows did not land: " + total);
            ctx.complete();
        });
    }

    /** Rising strike launches the target; Eight Gates powers the user up. */
    @GameTest(templateName = EMPTY_STRUCTURE)
    public void launchAndGates(TestContext ctx) {
        ServerPlayerEntity p = fighter(ctx, "leaf_rock_destroying_rise", "eight_gates_released_formation");
        LivingEntity zombie = dummy(ctx, EntityType.ZOMBIE);
        ctx.assertTrue(cast(p, "leaf_rock_destroying_rise") == CastResult.SUCCESS, "rise cast failed");
        ctx.assertTrue(zombie.getVelocity().y > 0.5, "target not launched: " + zombie.getVelocity());
        ctx.assertTrue(cast(p, "eight_gates_released_formation") == CastResult.SUCCESS, "gates cast failed");
        ctx.assertTrue(p.hasStatusEffect(StatusEffects.STRENGTH) && p.hasStatusEffect(StatusEffects.SPEED), "gates buff missing");
        ctx.complete();
    }
}
