package com.animecore.network;

import com.animecore.platform.AnimeNetworking;
import com.animecore.platform.AnimePacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/** C2S: quick-cast hotbar slot of a given power system (keybinds are per-mod, so slots never collide). */
public record HotbarCastPayload(String system, int slot, boolean pressed) implements CustomPayload {
    public static final Id<HotbarCastPayload> ID = AnimeNetworking.payloadId("animecore", "hotbar_cast");
    public static final PacketCodec<RegistryByteBuf, HotbarCastPayload> CODEC = PacketCodec.tuple(
            AnimePacketCodecs.STRING, HotbarCastPayload::system,
            AnimePacketCodecs.VAR_INT, HotbarCastPayload::slot,
            AnimePacketCodecs.BOOL, HotbarCastPayload::pressed,
            HotbarCastPayload::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
