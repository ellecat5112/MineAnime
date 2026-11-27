package com.animecore.client.vfx;

import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.entity.ManifestationEntity;
import com.animecore.manifestation.TimeStopPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Client copy of stopped-time regions: frozen entities stop animating and interpolating (see ClientWorldMixin). */
public final class TimeStopClient {
    private TimeStopClient() {}

    private record Region(Vec3d center, double radius, long end, Set<Integer> moving) {}

    private static final List<Region> REGIONS = new ArrayList<>();

    public static void register() {
        AnimeClientNetworking.registerReceiver(TimeStopPayload.ID, (p, client) -> {
            if (client.world == null) return;
            REGIONS.add(new Region(new Vec3d(p.x(), p.y(), p.z()), p.radius(), client.world.getTime() + p.ticks(), new HashSet<>(p.moving())));
        });
    }

    public static boolean frozen(Entity e) {
        if (REGIONS.isEmpty()) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || e == client.player) return false;
        long now = client.world.getTime();
        REGIONS.removeIf(r -> now >= r.end);
        int id = e instanceof ManifestationEntity m && m.owner() != null ? m.owner().getId() : e.getId();
        for (Region r : REGIONS) {
            if (e.squaredDistanceTo(r.center) <= r.radius * r.radius && !r.moving.contains(id) && !r.moving.contains(e.getId())) return true;
        }
        return false;
    }
}
