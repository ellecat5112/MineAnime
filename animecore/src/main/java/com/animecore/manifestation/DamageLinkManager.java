package com.animecore.manifestation;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Lovers' pain link: while linked, damage aimed at the caster is dealt to the host instead (see DamageLinkAction). */
public final class DamageLinkManager {
    private DamageLinkManager() {}

    private record Link(LivingEntity host, float ratio, long end) {}

    private static final Map<UUID, Link> LINKS = new HashMap<>();
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> false);

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            Link link = LINKS.get(entity.getUuid());
            if (link == null || ACTIVE.get()) return true;
            if (!link.host.isAlive() || link.host.isRemoved() || entity.getWorld().getTime() >= link.end) {
                LINKS.remove(entity.getUuid());
                return true;
            }
            if (source.isOf(net.minecraft.entity.damage.DamageTypes.OUT_OF_WORLD)) return true;
            ACTIVE.set(true);
            try {
                link.host.timeUntilRegen = 0;
                link.host.damage(entity.getDamageSources().magic(), amount * link.ratio);
                if (link.host.getWorld() instanceof ServerWorld w) {
                    w.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, link.host.getX(), link.host.getEyeY(), link.host.getZ(), 4, 0.2, 0.2, 0.2, 0.1);
                }
            } finally {
                ACTIVE.set(false);
            }
            return false;   // the caster feels nothing
        });
    }

    public static void link(LivingEntity caster, LivingEntity host, int ticks, float ratio) {
        LINKS.put(caster.getUuid(), new Link(host, ratio, caster.getWorld().getTime() + ticks));
    }

    public static boolean linked(LivingEntity caster) {
        Link l = LINKS.get(caster.getUuid());
        return l != null && l.host.isAlive() && caster.getWorld().getTime() < l.end;
    }
}
