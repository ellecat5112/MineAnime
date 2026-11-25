package com.animecore.client.network;

import com.animecore.client.animation.AnimationLayerManager;
import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.network.AnimationSyncPayload;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;

public final class AnimeClientPayloadHandlers {
    private AnimeClientPayloadHandlers() {}

    public static void init() {
        AnimeClientNetworking.registerReceiver(AnimationSyncPayload.ID, (payload, client) -> {
            if (client.world == null) return;
            Entity entity = client.world.getEntityById(payload.entityId());
            if (entity instanceof AbstractClientPlayerEntity player) AnimationLayerManager.apply(player, payload);
        });
    }
}
