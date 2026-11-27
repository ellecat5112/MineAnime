package com.animecore.manifestation;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Who is hiding inside Cream's void right now (untouchable until it ends); see VoidAction. */
public final class VoidManager {
    private VoidManager() {}

    private static final Map<UUID, Long> IN_VOID = new HashMap<>();

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) ->
                !inVoid(entity) || source.isOf(net.minecraft.entity.damage.DamageTypes.OUT_OF_WORLD));
    }

    public static void enter(LivingEntity e, int ticks) { IN_VOID.put(e.getUuid(), e.getWorld().getTime() + ticks); }

    public static boolean inVoid(LivingEntity e) {
        Long end = IN_VOID.get(e.getUuid());
        if (end == null) return false;
        if (e.getWorld().getTime() >= end) {
            IN_VOID.remove(e.getUuid());
            return false;
        }
        return true;
    }
}
