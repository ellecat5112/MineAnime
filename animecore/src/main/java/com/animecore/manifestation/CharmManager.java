package com.animecore.manifestation;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Mobs under someone's control ({@link com.animecore.ability.action.CharmAction}): they never attack their controller or
 * each other and hunt whatever the controller fights (its attacker, its target, or the nearest hostile creature).
 */
public final class CharmManager {
    private CharmManager() {}

    private record Charm(MobEntity mob, UUID controller, long end) {}

    private static final Map<UUID, Charm> CHARMED = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(CharmManager::tick);
    }

    public static void charm(MobEntity mob, LivingEntity controller, int ticks) {
        CHARMED.put(mob.getUuid(), new Charm(mob, controller.getUuid(), mob.getWorld().getTime() + ticks));
        mob.setTarget(null);
    }

    public static boolean isCharmedBy(LivingEntity e, UUID controller) {
        Charm c = CHARMED.get(e.getUuid());
        return c != null && c.controller.equals(controller);
    }

    private static LivingEntity nearestHostile(ServerWorld world, MobEntity mob, UUID controller) {
        LivingEntity best = null;
        double bestD = Double.MAX_VALUE;
        for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, mob.getBoundingBox().expand(16), e -> e != mob && e.isAlive()
                && !e.getUuid().equals(controller) && !isCharmedBy(e, controller)
                && (e instanceof Monster || e instanceof com.animecore.entity.StoryNpcEntity npc && npc.role().hostile()))) {
            double d = e.squaredDistanceTo(mob);
            if (d < bestD) { bestD = d; best = e; }
        }
        return best;
    }

    private static void tick(MinecraftServer server) {
        if (CHARMED.isEmpty()) return;
        Iterator<Charm> it = CHARMED.values().iterator();
        while (it.hasNext()) {
            Charm c = it.next();
            MobEntity mob = c.mob;
            if (!mob.isAlive() || mob.isRemoved() || mob.getWorld().getTime() >= c.end) {
                it.remove();
                if (mob.isAlive()) mob.setTarget(null);
                continue;
            }
            if (!(mob.getWorld() instanceof ServerWorld world)) continue;
            LivingEntity controller = world.getEntity(c.controller) instanceof LivingEntity l ? l : null;
            LivingEntity target = mob.getTarget();
            if (target != null && (target.getUuid().equals(c.controller) || isCharmedBy(target, c.controller))) {
                mob.setTarget(null);
                target = null;
            }
            if ((target == null || !target.isAlive()) && controller != null && mob.age % 10 == 0) {
                LivingEntity enemy = controller.getAttacker() != null ? controller.getAttacker() : controller.getAttacking();
                if (enemy == null || !enemy.isAlive() || enemy == mob || isCharmedBy(enemy, c.controller)) {
                    enemy = nearestHostile(world, mob, c.controller);
                }
                if (enemy != null && enemy != mob && enemy != controller) mob.setTarget(enemy);
            }
            if (mob.age % 20 == 0) world.spawnParticles(ParticleTypes.WITCH, mob.getX(), mob.getEyeY() + 0.3, mob.getZ(), 2, 0.2, 0.1, 0.2, 0.0);
        }
    }
}
