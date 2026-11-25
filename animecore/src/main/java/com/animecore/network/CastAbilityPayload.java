package com.animecore.network;

import com.animecore.platform.AnimeNetworking;
import com.animecore.platform.AnimePacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** C2S: key pressed / released for a specific ability (press = cast/charge/arm, release = fire charge). */
public record CastAbilityPayload(Identifier ability, boolean pressed) implements CustomPayload {
    public static final Id<CastAbilityPayload> ID = AnimeNetworking.payloadId("animecore", "cast_ability");
    public static final PacketCodec<RegistryByteBuf, CastAbilityPayload> CODEC = PacketCodec.tuple(
            AnimePacketCodecs.IDENTIFIER, CastAbilityPayload::ability,
            AnimePacketCodecs.BOOL, CastAbilityPayload::pressed,
            CastAbilityPayload::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
