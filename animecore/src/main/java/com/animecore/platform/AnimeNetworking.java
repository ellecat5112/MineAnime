package com.animecore.platform;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.BiConsumer;

/**
 * Version shield for the 1.20.5+ {@link CustomPayload} networking stack (common/server side only).
 * Client registration lives in {@code com.animecore.client.platform.AnimeClientNetworking}.
 */
public final class AnimeNetworking {
    private AnimeNetworking() {}

    /** Payload ids are always namespaced by the owning mod: {@code <modId>:<path>}. */
    public static <T extends CustomPayload> CustomPayload.Id<T> payloadId(String modId, String path) {
        return new CustomPayload.Id<>(Ids.of(modId, path));
    }

    public static <T extends CustomPayload> void registerS2C(CustomPayload.Id<T> id, PacketCodec<? super RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(id, codec);
    }

    public static <T extends CustomPayload> void registerC2S(CustomPayload.Id<T> id, PacketCodec<? super RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(id, codec);
    }

    /** Handler runs on the server thread (guaranteed by Fabric API since 1.20.5). */
    public static <T extends CustomPayload> void registerServerReceiver(CustomPayload.Id<T> id, BiConsumer<T, ServerPlayerEntity> handler) {
        ServerPlayNetworking.registerGlobalReceiver(id, (payload, context) -> handler.accept(payload, context.player()));
    }

    public static void send(ServerPlayerEntity player, CustomPayload payload) {
        if (ServerPlayNetworking.canSend(player, payload.getId())) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    /** Sends to every peer tracking {@code entity}, plus the entity itself when it is a player (Essential P2P safe). */
    public static void sendToTrackingAndSelf(Entity entity, CustomPayload payload) {
        for (ServerPlayerEntity tracker : PlayerLookup.tracking(entity)) {
            if (tracker != entity) send(tracker, payload);
        }
        if (entity instanceof ServerPlayerEntity self) send(self, payload);
    }

    public static void sendToTracking(Entity entity, CustomPayload payload) {
        for (ServerPlayerEntity tracker : PlayerLookup.tracking(entity)) {
            if (tracker != entity) send(tracker, payload);
        }
    }
}
