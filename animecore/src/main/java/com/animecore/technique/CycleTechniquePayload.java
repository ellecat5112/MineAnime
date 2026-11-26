package com.animecore.technique;

import com.animecore.platform.AnimeNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;

/** Client -> server: switch the selected technique on the token in {@code hand} by {@code delta} (sneak + scroll / left-click). */
public record CycleTechniquePayload(Hand hand, int delta) implements CustomPayload {
    public static final Id<CycleTechniquePayload> ID = AnimeNetworking.payloadId("animecore", "cycle_technique");
    public static final PacketCodec<RegistryByteBuf, CycleTechniquePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL.xmap(b -> b ? Hand.MAIN_HAND : Hand.OFF_HAND, h -> h == Hand.MAIN_HAND), CycleTechniquePayload::hand,
            PacketCodecs.VAR_INT, CycleTechniquePayload::delta,
            CycleTechniquePayload::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
