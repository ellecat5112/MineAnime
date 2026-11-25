package com.animecore.animation;

import com.animecore.network.AnimationSyncPayload;
import com.animecore.platform.AnimeNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-authoritative animation broadcaster. Every pose is sent to the player <b>and</b> to all peers tracking
 * them, so Essential-hosted worlds and dedicated servers look identical for everybody.
 */
public final class AnimationService {
    private static final Map<UUID, EnumMap<AnimLayer, AnimationSyncPayload>> PERSISTENT = new ConcurrentHashMap<>();

    private AnimationService() {}

    public static void play(ServerPlayerEntity player, AnimLayer layer, Identifier animation, AnimPlayback playback) {
        AnimationSyncPayload payload = new AnimationSyncPayload(player.getId(), layer, Optional.of(animation),
                playback.fadeTicks(), playback.speed(), playback.upperBodyOnly());
        if (playback.persistent()) {
            PERSISTENT.computeIfAbsent(player.getUuid(), u -> new EnumMap<>(AnimLayer.class)).put(layer, payload);
        } else {
            clearPersistent(player, layer);
        }
        AnimeNetworking.sendToTrackingAndSelf(player, payload);
    }

    public static void stop(ServerPlayerEntity player, AnimLayer layer, int fadeTicks) {
        clearPersistent(player, layer);
        AnimeNetworking.sendToTrackingAndSelf(player, new AnimationSyncPayload(player.getId(), layer, Optional.empty(), fadeTicks, 1f, false));
    }

    /** Late-joiner sync: hook for {@code EntityTrackingEvents.START_TRACKING}. */
    public static void onStartTracking(Entity tracked, ServerPlayerEntity tracker) {
        if (!(tracked instanceof ServerPlayerEntity player)) return;
        EnumMap<AnimLayer, AnimationSyncPayload> active = PERSISTENT.get(player.getUuid());
        if (active == null) return;
        active.values().forEach(p -> AnimeNetworking.send(tracker,
                new AnimationSyncPayload(player.getId(), p.layer(), p.animation(), 0, p.speed(), p.upperBodyOnly())));
    }

    public static void onDisconnect(ServerPlayerEntity player) {
        PERSISTENT.remove(player.getUuid());
    }

    private static void clearPersistent(ServerPlayerEntity player, AnimLayer layer) {
        EnumMap<AnimLayer, AnimationSyncPayload> map = PERSISTENT.get(player.getUuid());
        if (map != null) {
            map.remove(layer);
            if (map.isEmpty()) PERSISTENT.remove(player.getUuid());
        }
    }
}
