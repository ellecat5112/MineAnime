package com.animecore.origin;

import com.animecore.platform.AnimeNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/** Server -> client: open the origin (village + background) selection screen for {@code system}. */
public record OpenOriginScreenPayload(String system) implements CustomPayload {
    public static final Id<OpenOriginScreenPayload> ID = AnimeNetworking.payloadId("animecore", "open_origin");
    public static final PacketCodec<RegistryByteBuf, OpenOriginScreenPayload> CODEC =
            PacketCodecs.STRING.xmap(OpenOriginScreenPayload::new, OpenOriginScreenPayload::system).cast();

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
