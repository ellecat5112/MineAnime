package com.animecore.movement;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.api.EnergyPoolSpec;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.network.MovementPayloads;
import com.animecore.platform.AnimeNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

/**
 * Server-authoritative anime mobility. Physics for adhesion is done by vanilla (ladder / strider rules via
 * {@code LivingEntityMixin}); flight reuses vanilla abilities so anti-cheat and client prediction just work.
 */
public final class AnimeMovementHandler {
    /** Continuous modes drain this fraction of their cost while toggled on but not actively in use. */
    public static final float IDLE_DRAIN_FACTOR = 0.2f;

    private AnimeMovementHandler() {}

    // ------------------------------------------------------------------ queries (both sides)

    /** True if any power system has surface adhesion active for this player (read by the mixin every tick). */
    public static boolean isAdhering(PlayerEntity player) {
        if (player.isSpectator()) return false;
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            Optional<PlayerPowerComponent> power = ctx.maybePower(player);
            if (power.isPresent() && power.get().isMovementActive(MovementMode.SURFACE_ADHESION)) return true;
        }
        return false;
    }

    // ------------------------------------------------------------------ toggles

    public static boolean toggle(ServerPlayerEntity player, AnimeModContext ctx, MovementMode mode) {
        if (!mode.continuous()) return false;
        Optional<MovementSpec> spec = ctx.movement(mode);
        if (spec.isEmpty()) return false;
        PlayerPowerComponent power = ctx.power(player);
        if (power.isMovementActive(mode)) {
            deactivate(player, power, mode);
            player.sendMessage(Text.translatable("message.animecore.movement.off", Text.translatable(mode.translationKey()))
                    .formatted(Formatting.GRAY), true);
            return true;
        }
        if (power.tier() < spec.get().requiredTier()) return deny(player, "message.animecore.cast.tier_too_low");
        if (!power.tryConsume(spec.get().pool(), spec.get().activationCost())) return deny(player, "message.animecore.cast.no_energy");
        power.setMovementActive(mode, true);
        if (mode == MovementMode.OMNIDIRECTIONAL_FLIGHT) grantFlight(player, power, spec.get().strength());
        player.sendMessage(Text.translatable("message.animecore.movement.on", Text.translatable(mode.translationKey()))
                .formatted(Formatting.AQUA), true);
        return true;
    }

    public static void deactivate(ServerPlayerEntity player, PlayerPowerComponent power, MovementMode mode) {
        power.setMovementActive(mode, false);
        if (mode == MovementMode.OMNIDIRECTIONAL_FLIGHT) revokeFlight(player, power);
    }

    // ------------------------------------------------------------------ upkeep

    /** Called from the component's server tick. */
    public static void tick(ServerPlayerEntity player, PlayerPowerComponent power) {
        // Flight granted in a previous session (abilities persist in player NBT, our toggle state does not).
        if (power.flightGranted() && !power.isMovementActive(MovementMode.OMNIDIRECTIONAL_FLIGHT)) revokeFlight(player, power);

        for (MovementMode mode : MovementMode.values()) {
            if (!mode.continuous() || !power.isMovementActive(mode)) continue;
            Optional<MovementSpec> spec = power.context().movement(mode);
            if (spec.isEmpty()) {
                deactivate(player, power, mode);
                continue;
            }
            float drain = spec.get().drainPerTick() * (inUse(player, mode) ? 1f : IDLE_DRAIN_FACTOR);
            if (!power.tryConsume(spec.get().pool(), drain)) {
                deactivate(player, power, mode);
                player.sendMessage(Text.translatable("message.animecore.movement.exhausted", Text.translatable(mode.translationKey()))
                        .formatted(Formatting.RED), true);
            }
        }
    }

    private static boolean inUse(ServerPlayerEntity player, MovementMode mode) {
        return switch (mode) {
            case SURFACE_ADHESION -> (player.horizontalCollision && player.isClimbing())
                    || player.getWorld().getFluidState(player.getBlockPos().down()).isIn(FluidTags.WATER);
            case OMNIDIRECTIONAL_FLIGHT -> player.getAbilities().flying;
            case BURST_STEP -> false;
        };
    }

    // ------------------------------------------------------------------ flight

    private static void grantFlight(ServerPlayerEntity player, PlayerPowerComponent power, float speed) {
        PlayerAbilities abilities = player.getAbilities();
        if (!abilities.allowFlying) {
            abilities.allowFlying = true;
            power.setFlightGranted(true);
        }
        abilities.flying = true;
        abilities.setFlySpeed(speed > 0 ? speed : 0.05f);
        player.sendAbilitiesUpdate();
    }

    private static void revokeFlight(ServerPlayerEntity player, PlayerPowerComponent power) {
        if (power.flightGranted() && player.interactionManager.getGameMode().isSurvivalLike()) {
            PlayerAbilities abilities = player.getAbilities();
            boolean wasFlying = abilities.flying;
            abilities.allowFlying = false;
            abilities.flying = false;
            abilities.setFlySpeed(0.05f);
            player.sendAbilitiesUpdate();
            if (wasFlying) player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 60, 0, false, false, true));
        }
        power.setFlightGranted(false);
        player.fallDistance = 0f;
    }

    // ------------------------------------------------------------------ burst step

    /** @return true if the blink happened */
    public static boolean burstStep(ServerPlayerEntity player, AnimeModContext ctx, float forward, float strafe) {
        Optional<MovementSpec> spec = ctx.movement(MovementMode.BURST_STEP);
        if (spec.isEmpty()) return false;
        PlayerPowerComponent power = ctx.power(player);
        Identifier cooldownKey = ctx.id("movement/burst_step");
        if (power.tier() < spec.get().requiredTier()) return deny(player, "message.animecore.cast.tier_too_low");
        if (power.isOnCooldown(cooldownKey)) return false; // silent: players spam dash keys
        if (!power.tryConsume(spec.get().pool(), spec.get().activationCost())) return deny(player, "message.animecore.cast.no_energy");

        float f = Math.max(-1f, Math.min(1f, forward));
        float s = Math.max(-1f, Math.min(1f, strafe));
        Vec3d facing = Vec3d.fromPolar(0, player.getYaw());
        Vec3d left = Vec3d.fromPolar(0, player.getYaw() - 90f);
        Vec3d dir = facing.multiply(f).add(left.multiply(s));
        if (dir.lengthSquared() < 1.0E-4) dir = facing;
        if (player.getAbilities().flying) dir = dir.add(0, -Math.sin(Math.toRadians(player.getPitch())), 0);
        dir = dir.normalize();

        Vec3d start = player.getPos();
        Vec3d destination = MotionUtil.clipDestination(player.getWorld(), player, start, start.add(dir.multiply(spec.get().strength())));
        int color = ctx.pool(spec.get().pool()).map(EnergyPoolSpec::color).orElse(0xFFFFFF);
        AnimeNetworking.sendToTrackingAndSelf(player, new MovementPayloads.Afterimage(player.getId(),
                start.toVector3f(), destination.toVector3f(), color));

        player.networkHandler.requestTeleport(destination.x, destination.y, destination.z, player.getYaw(), player.getPitch());
        player.fallDistance = 0f;
        power.setCooldown(cooldownKey, spec.get().cooldownTicks());
        player.getServerWorld().playSound(null, start.x, start.y, start.z, SoundEvents.ENTITY_PHANTOM_FLAP, SoundCategory.PLAYERS, 0.6f, 1.8f);
        return true;
    }

    private static boolean deny(ServerPlayerEntity player, String key) {
        player.sendMessage(Text.translatable(key).formatted(Formatting.RED), true);
        return false;
    }
}
