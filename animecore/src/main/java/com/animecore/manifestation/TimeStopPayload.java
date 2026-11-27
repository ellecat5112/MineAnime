package com.animecore.manifestation;

import com.animecore.platform.AnimeNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

/** Tells clients where time stopped (so they stop animating frozen entities too) and who keeps moving. */
public record TimeStopPayload(double x, double y, double z, float radius, int ticks, List<Integer> moving) implements CustomPayload {
    public static final Id<TimeStopPayload> ID = AnimeNetworking.payloadId("animecore", "time_stop");
    public static final PacketCodec<RegistryByteBuf, TimeStopPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE, TimeStopPayload::x,
            PacketCodecs.DOUBLE, TimeStopPayload::y,
            PacketCodecs.DOUBLE, TimeStopPayload::z,
            PacketCodecs.FLOAT, TimeStopPayload::radius,
            PacketCodecs.VAR_INT, TimeStopPayload::ticks,
            PacketCodecs.VAR_INT.collect(PacketCodecs.toList()), TimeStopPayload::moving,
            TimeStopPayload::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
