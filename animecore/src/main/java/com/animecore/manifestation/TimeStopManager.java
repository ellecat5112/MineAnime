package com.animecore.manifestation;

import com.animecore.clan.ClanTraits;
import com.animecore.platform.AnimeNetworking;
import com.animecore.vfx.ScreenEffect;
import com.animecore.vfx.ScreenEffectPayload;
import com.animecore.vfx.VfxStyle;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Stopped time (The World, Star Platinum: The World). Inside a stop's radius every entity except the caster and
 * time-immune beings ({@link ClanTraits.TimeImmune} through their Stand or lineage) stops ticking: mobs freeze mid-step,
 * projectiles and thrown knives hang in the air, and the velocity of every blow is kept until time resumes. Frozen
 * players cannot move (speed / jump / gravity zeroed through attributes, so it also holds client-side), attack, use
 * items or cast. Repeated hits on a frozen target all land (no hurt cooldown while time is stopped).
 */
public final class TimeStopManager {
    private TimeStopManager() {}

    private record Stop(ServerWorld world, UUID caster, Vec3d center, double radius, long end) {
        boolean covers(Entity e) {
            return e.getWorld() == world && e.squaredDistanceTo(center) <= radius * radius;
        }
    }

    private static final List<Stop> STOPS = new ArrayList<>();
    private static final Map<UUID, Vec3d> FROZEN_PLAYERS = new HashMap<>();
    private static final Identifier FREEZE = Identifier.of("animecore", "time_stop");
    private static final List<RegistryEntry<EntityAttribute>> LOCKED = List.of(EntityAttributes.GENERIC_MOVEMENT_SPEED,
            EntityAttributes.GENERIC_JUMP_STRENGTH, EntityAttributes.GENERIC_GRAVITY, EntityAttributes.GENERIC_FLYING_SPEED);

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TimeStopManager::tick);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (isFrozen(entity)) entity.timeUntilRegen = 0;   // every blow during stopped time lands
            return true;
        });
        AttackEntityCallback.EVENT.register((p, w, h, e, hit) -> frozenPlayer(p) ? ActionResult.FAIL : ActionResult.PASS);
        AttackBlockCallback.EVENT.register((p, w, h, pos, dir) -> frozenPlayer(p) ? ActionResult.FAIL : ActionResult.PASS);
        UseBlockCallback.EVENT.register((p, w, h, hit) -> frozenPlayer(p) ? ActionResult.FAIL : ActionResult.PASS);
        UseEntityCallback.EVENT.register((p, w, h, e, hit) -> frozenPlayer(p) ? ActionResult.FAIL : ActionResult.PASS);
        UseItemCallback.EVENT.register((p, w, h) -> frozenPlayer(p) ? TypedActionResult.fail(p.getStackInHand(h)) : TypedActionResult.pass(p.getStackInHand(h)));
        com.animecore.api.event.AnimeCoreEvents.PRE_ABILITY_CAST.register((player, ability, ctx) ->
                isFrozen(player) ? ActionResult.FAIL : ActionResult.PASS);
    }

    private static boolean frozenPlayer(PlayerEntity p) { return !p.getWorld().isClient && isFrozen(p); }

    /** Stops time around {@code caster} for {@code ticks}. Players caught inside see the time-stop screen effect. */
    public static void stop(LivingEntity caster, double radius, int ticks) {
        if (!(caster.getWorld() instanceof ServerWorld world)) return;
        Stop stop = new Stop(world, caster.getUuid(), caster.getPos(), radius, world.getTime() + ticks);
        STOPS.add(stop);
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.PLAYERS, 2f, 0.5f);
        world.playSound(null, caster.getBlockPos(), SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.PLAYERS, 2f, 0.6f);
        List<Integer> moving = new ArrayList<>();
        moving.add(caster.getId());
        for (Entity e : world.getOtherEntities(caster, caster.getBoundingBox().expand(radius), stop::covers)) {
            if (immune(e) || e instanceof com.animecore.entity.ManifestationEntity m && m.owner() == caster) moving.add(e.getId());
        }
        TimeStopPayload sync = new TimeStopPayload(stop.center.x, stop.center.y, stop.center.z, (float) radius, ticks, moving);
        for (ServerPlayerEntity p : world.getPlayers()) {
            if (p.squaredDistanceTo(stop.center) < (radius + 64) * (radius + 64)) AnimeNetworking.send(p, sync);
            if (stop.covers(p)) AnimeNetworking.send(p, new ScreenEffectPayload(ScreenEffect.TIME_STOP, VfxStyle.LIGHTNING, 0x7E57C2, ticks));
        }
        for (Entity e : world.getOtherEntities(caster, caster.getBoundingBox().expand(radius), stop::covers)) {
            if (e instanceof ServerPlayerEntity p && isFrozen(p)) freezePlayer(p);
        }
    }

    public static boolean active() { return !STOPS.isEmpty(); }

    /** Whether {@code e} is held in stopped time right now. */
    public static boolean isFrozen(Entity e) {
        if (STOPS.isEmpty() || e.getWorld().isClient) return false;
        @Nullable Stop covering = null;
        for (Stop s : STOPS) {
            if (s.covers(e)) {
                if (s.caster.equals(e.getUuid()) || e instanceof com.animecore.entity.ManifestationEntity m && m.owner() != null
                        && s.caster.equals(m.owner().getUuid())) return false;
                covering = s;
            }
        }
        return covering != null && !immune(e);
    }

    /** Time-immune beings move freely in stopped time (Star Platinum and The World can enter each other's). */
    public static boolean immune(Entity e) {
        if (ManifestationService.hasTrait(e, ClanTraits.TimeImmune.class)) return true;
        if (e instanceof ServerPlayerEntity p) {
            for (var ctx : com.animecore.api.AnimeModRegistry.all()) {
                var power = ctx.maybePower(p).orElse(null);
                if (power != null && com.animecore.clan.ClanTraitHooks.traits(p, power).stream().anyMatch(t -> t instanceof ClanTraits.TimeImmune)) return true;
            }
        }
        return false;
    }

    private static void freezePlayer(ServerPlayerEntity p) {
        if (FROZEN_PLAYERS.containsKey(p.getUuid())) return;
        FROZEN_PLAYERS.put(p.getUuid(), p.getPos());
        for (RegistryEntry<EntityAttribute> a : LOCKED) {
            EntityAttributeInstance inst = p.getAttributeInstance(a);
            if (inst != null && !inst.hasModifier(FREEZE)) {
                inst.addTemporaryModifier(new EntityAttributeModifier(FREEZE, -1.0, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
        }
        p.setVelocity(Vec3d.ZERO);
        p.velocityModified = true;
    }

    private static void release(ServerPlayerEntity p) {
        FROZEN_PLAYERS.remove(p.getUuid());
        for (RegistryEntry<EntityAttribute> a : LOCKED) {
            EntityAttributeInstance inst = p.getAttributeInstance(a);
            if (inst != null) inst.removeModifier(FREEZE);
        }
    }

    private static void tick(MinecraftServer server) {
        if (STOPS.isEmpty() && FROZEN_PLAYERS.isEmpty()) return;
        boolean ended = STOPS.removeIf(s -> s.world.getTime() >= s.end);
        if (ended) {
            for (ServerWorld w : server.getWorlds()) {
                if (STOPS.stream().noneMatch(s -> s.world == w)) {
                    for (ServerPlayerEntity p : w.getPlayers()) {
                        if (FROZEN_PLAYERS.containsKey(p.getUuid())) w.playSound(null, p.getBlockPos(), SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1f, 1.4f);
                    }
                }
            }
        }
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            boolean frozen = isFrozen(p);
            if (frozen) {
                freezePlayer(p);
                Vec3d at = FROZEN_PLAYERS.get(p.getUuid());
                if (at != null && p.getPos().squaredDistanceTo(at) > 0.5) p.networkHandler.requestTeleport(at.x, at.y, at.z, p.getYaw(), p.getPitch());
            } else if (FROZEN_PLAYERS.containsKey(p.getUuid())) {
                release(p);
            }
        }
    }
}
