package com.animecore.client.platform;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.CustomPayload;

import java.util.function.BiConsumer;

/** Client half of the networking version shield. */
public final class AnimeClientNetworking {
    private AnimeClientNetworking() {}

    /** Handler runs on the render thread (guaranteed by Fabric API since 1.20.5). */
    public static <T extends CustomPayload> void registerReceiver(CustomPayload.Id<T> id, BiConsumer<T, MinecraftClient> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, (payload, context) -> handler.accept(payload, context.client()));
    }

    public static void send(CustomPayload payload) {
        if (ClientPlayNetworking.canSend(payload.getId())) ClientPlayNetworking.send(payload);
    }
}
