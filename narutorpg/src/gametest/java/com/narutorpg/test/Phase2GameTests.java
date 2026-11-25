package com.narutorpg.test;

import com.animecore.ability.AbilityExecutor;
import com.animecore.ability.CastResult;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.movement.AnimeMovementHandler;
import com.animecore.movement.MovementMode;
import com.animecore.transformation.TransformationService;
import com.narutorpg.NarutoRPG;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import static com.narutorpg.content.NarutoContent.*;

/** Phase 2: mobility, stage handler, multi-system coexistence. */
@SuppressWarnings("removal") // createMockCreativeServerPlayerInWorld is the 1.21.1 API
public final class Phase2GameTests implements FabricGameTest {

    private static ServerPlayerEntity player(TestContext ctx) {
        ServerPlayerEntity player = ctx.createMockCreativeServerPlayerInWorld();
        player.changeGameMode(GameMode.SURVIVAL);
        return player;
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void chakraControlWallAndWaterWalking(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        player.horizontalCollision = true;
        ctx.assertTrue(!player.isClimbing(), "should not climb walls without chakra control");
        ctx.assertTrue(AnimeMovementHandler.toggle(player, NarutoRPG.CTX, MovementMode.SURFACE_ADHESION), "toggle failed");
        ctx.assertTrue(NarutoRPG.CTX.power(player).isMovementActive(MovementMode.SURFACE_ADHESION), "adhesion not active");
        ctx.assertTrue(player.isClimbing(), "wall should act as a ladder");
        ctx.assertTrue(player.canWalkOnFluid(Fluids.WATER.getDefaultState()), "should walk on water");
        ctx.assertTrue(!player.canWalkOnFluid(Fluids.LAVA.getDefaultState()), "lava is not walkable");
        player.setSneaking(true);
        ctx.assertTrue(!player.canWalkOnFluid(Fluids.WATER.getDefaultState()), "sneak should let the player dive");
        player.setSneaking(false);
        AnimeMovementHandler.toggle(player, NarutoRPG.CTX, MovementMode.SURFACE_ADHESION);
        ctx.assertTrue(!player.isClimbing(), "adhesion should be off");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void adhesionEndsWhenChakraRunsOut(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        AnimeMovementHandler.toggle(player, NarutoRPG.CTX, MovementMode.SURFACE_ADHESION);
        power.pool(CHAKRA).orElseThrow().setBaseRegen(0f);
        power.setEnergy(CHAKRA, 0f);
        power.serverTick();
        ctx.assertTrue(!power.isMovementActive(MovementMode.SURFACE_ADHESION), "adhesion should end without chakra");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void shunshinBlinksForwardWithCooldown(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        Vec3d start = ctx.getAbsolute(new Vec3d(0.5, 120, 0.5)); // open sky: nothing to clip against
        player.refreshPositionAndAngles(start.x, start.y, start.z, 0f, 0f); // yaw 0 = +Z
        float chakra = power.pool(CHAKRA).orElseThrow().current();
        ctx.assertTrue(AnimeMovementHandler.burstStep(player, NarutoRPG.CTX, 1f, 0f), "burst step failed");
        double moved = player.getPos().subtract(start).horizontalLength();
        ctx.assertTrue(Math.abs(moved - 7.0) < 0.05, "expected 7 blocks, moved " + moved);
        ctx.assertTrue(player.getZ() > start.z, "should move forward (+Z)");
        ctx.assertTrue(Math.abs(chakra - power.pool(CHAKRA).orElseThrow().current() - 6f) < 1e-3, "6 chakra expected");
        ctx.assertTrue(!AnimeMovementHandler.burstStep(player, NarutoRPG.CTX, 1f, 0f), "should be on cooldown");
        // strafe left (A) = +X when facing +Z
        power.setCooldown(NarutoRPG.CTX.id("movement/burst_step"), 0);
        double x = player.getX();
        AnimeMovementHandler.burstStep(player, NarutoRPG.CTX, 0f, 1f);
        ctx.assertTrue(player.getX() > x + 6.9, "strafe-left should move +X, moved " + (player.getX() - x));
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void secondSystemFlightCoexistsWithNaruto(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent ki = FlightTestSystem.CTX.power(player);
        PlayerPowerComponent chakra = NarutoRPG.CTX.power(player);
        ctx.assertTrue(ki != chakra, "systems must have separate components");
        ctx.assertTrue(ki.pool(CHAKRA).isEmpty() && chakra.pool(FlightTestSystem.CTX.id("ki")).isEmpty(), "pools leaked across systems");

        ctx.assertTrue(AnimeMovementHandler.toggle(player, FlightTestSystem.CTX, MovementMode.OMNIDIRECTIONAL_FLIGHT), "flight toggle failed");
        ctx.assertTrue(player.getAbilities().allowFlying && player.getAbilities().flying, "flight not granted");
        ctx.assertTrue(Math.abs(player.getAbilities().getFlySpeed() - 0.08f) < 1e-6, "fly speed not applied");
        ctx.assertTrue(!AnimeMovementHandler.toggle(player, NarutoRPG.CTX, MovementMode.OMNIDIRECTIONAL_FLIGHT), "naruto has no flight");
        ki.serverTick();
        ctx.assertTrue(ki.pool(FlightTestSystem.CTX.id("ki")).orElseThrow().current() < 200f, "ki should drain while flying");

        AnimeMovementHandler.toggle(player, FlightTestSystem.CTX, MovementMode.OMNIDIRECTIONAL_FLIGHT);
        ctx.assertTrue(!player.getAbilities().allowFlying, "flight not revoked (creative=" + player.isCreative()
                + ", mode=" + player.interactionManager.getGameMode() + ")");

        // Relog simulation: vanilla persisted mayfly, our toggle did not -> must be revoked on next tick.
        player.getAbilities().allowFlying = true;
        ki.setFlightGranted(true);
        ki.serverTick();
        ctx.assertTrue(!player.getAbilities().allowFlying && !ki.flightGranted(), "stale flight survived relog");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void sharinganStageHandler(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        PlayerPowerComponent power = NarutoRPG.CTX.power(player);
        power.setLineage(UCHIHA.getValue());
        power.setTier(5);
        power.unlockTransformation(SHARINGAN_2.getValue());
        power.serverTick();

        ctx.assertTrue(TransformationService.toggle(player, power, SHARINGAN_1.getValue()), "1-tomoe failed");
        ctx.assertTrue(TransformationService.ascend(player, power), "ascend failed");
        ctx.assertTrue(SHARINGAN_2.getValue().equals(power.activeTransformation().orElse(null)), "should be 2-tomoe");
        ctx.assertTrue(!TransformationService.ascend(player, power), "3-tomoe is not unlocked");

        // Depletion: 2-tomoe drain 0.06 unaffordable, 1-tomoe drain 0.04 affordable -> drop a stage, not revert.
        power.pool(CHAKRA).orElseThrow().setBaseRegen(0f);
        power.setEnergy(CHAKRA, 0.05f);
        power.serverTick();
        ctx.assertTrue(SHARINGAN_1.getValue().equals(power.activeTransformation().orElse(null)),
                "should fall back to 1-tomoe, got " + power.activeTransformation());

        power.setEnergy(CHAKRA, 0f);
        power.serverTick();
        ctx.assertTrue(power.activeTransformation().isEmpty(), "should revert when nothing is affordable");
        ctx.assertTrue(TransformationService.previousStage(player, MANGEKYO.getValue()).orElseThrow().equals(SHARINGAN_3.getValue()),
                "reverse stage lookup");
        ctx.complete();
    }

    @GameTest(templateName = EMPTY_STRUCTURE)
    public void comboReleaseAfterAutoCastIsSilent(TestContext ctx) {
        ServerPlayerEntity player = player(ctx);
        CastResult result = AbilityExecutor.handleComboInput(player, NarutoRPG.CTX, NarutoRPG.CTX.id("release"), true);
        ctx.assertTrue(result == CastResult.CANCELLED, "empty release should be a silent no-op, got " + result);
        ctx.complete();
    }
}
