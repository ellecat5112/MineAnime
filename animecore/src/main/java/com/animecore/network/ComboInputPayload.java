package com.animecore.network;

import com.animecore.platform.AnimeNetworking;
import com.animecore.platform.AnimePacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** C2S: one combo token (hand sign / incantation word / direction). {@code commit=true} = "release" the combo. */
public record ComboInputPayload(String system, Identifier token, boolean commit) implements CustomPayload {
    public static final Id<ComboInputPayload> ID = AnimeNetworking.payloadId("animecore", "combo_input");
    public static final PacketCodec<RegistryByteBuf, ComboInputPayload> CODEC = PacketCodec.tuple(
            AnimePacketCodecs.STRING, ComboInputPayload::system,
            AnimePacketCodecs.IDENTIFIER, ComboInputPayload::token,
            AnimePacketCodecs.BOOL, ComboInputPayload::commit,
            ComboInputPayload::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
