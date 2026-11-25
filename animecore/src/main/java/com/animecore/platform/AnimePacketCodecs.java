package com.animecore.platform;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.Optional;

/** Version shield for frequently used {@link PacketCodec}s. */
public final class AnimePacketCodecs {
    private AnimePacketCodecs() {}

    public static final PacketCodec<ByteBuf, Identifier> IDENTIFIER = Identifier.PACKET_CODEC;
    public static final PacketCodec<ByteBuf, Optional<Identifier>> OPTIONAL_IDENTIFIER = PacketCodecs.optional(Identifier.PACKET_CODEC);
    public static final PacketCodec<ByteBuf, String> STRING = PacketCodecs.STRING;
    public static final PacketCodec<ByteBuf, Integer> VAR_INT = PacketCodecs.VAR_INT;
    public static final PacketCodec<ByteBuf, Float> FLOAT = PacketCodecs.FLOAT;
    public static final PacketCodec<ByteBuf, Boolean> BOOL = PacketCodecs.BOOL;

    /** Ordinal-based enum codec (enums must only ever be appended to). */
    public static <E extends Enum<E>> PacketCodec<ByteBuf, E> enumCodec(Class<E> type) {
        E[] values = type.getEnumConstants();
        return PacketCodecs.VAR_INT.xmap(i -> values[Math.floorMod(i, values.length)], Enum::ordinal);
    }
}
