package com.animecore.origin;

import com.animecore.platform.AnimeNetworking;
import com.animecore.platform.AnimePacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Optional;

/** Client -> server: the chosen village + background (or {@code random}). Validated by {@link OriginSelectionService#select}. */
public record SelectOriginPayload(String system, Optional<Identifier> faction, Optional<Identifier> background, boolean random)
        implements CustomPayload {
    public static final Id<SelectOriginPayload> ID = AnimeNetworking.payloadId("animecore", "select_origin");
    public static final PacketCodec<RegistryByteBuf, SelectOriginPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, SelectOriginPayload::system,
            AnimePacketCodecs.OPTIONAL_IDENTIFIER, SelectOriginPayload::faction,
            AnimePacketCodecs.OPTIONAL_IDENTIFIER, SelectOriginPayload::background,
            PacketCodecs.BOOL, SelectOriginPayload::random,
            SelectOriginPayload::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
