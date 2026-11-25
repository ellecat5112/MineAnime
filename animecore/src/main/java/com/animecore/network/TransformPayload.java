package com.animecore.network;

import com.animecore.platform.AnimeNetworking;
import com.animecore.platform.AnimePacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Optional;

/** C2S: toggle a transformation. Empty form = toggle the best unlocked form (or revert if transformed). */
public record TransformPayload(String system, Optional<Identifier> form) implements CustomPayload {
    public static final Id<TransformPayload> ID = AnimeNetworking.payloadId("animecore", "transform");
    public static final PacketCodec<RegistryByteBuf, TransformPayload> CODEC = PacketCodec.tuple(
            AnimePacketCodecs.STRING, TransformPayload::system,
            AnimePacketCodecs.OPTIONAL_IDENTIFIER, TransformPayload::form,
            TransformPayload::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
