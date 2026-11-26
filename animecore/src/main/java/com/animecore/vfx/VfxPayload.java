package com.animecore.vfx;

import com.animecore.platform.AnimeNetworking;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

/** One-shot technique effect; clients render it procedurally (no textures, ~40 bytes). */
public record VfxPayload(VfxKind kind, VfxStyle style, int color, int entityId, Vec3d origin, Vec3d dir, float size, float extra)
        implements CustomPayload {
    public static final Id<VfxPayload> ID = AnimeNetworking.payloadId("animecore", "vfx");

    public static final PacketCodec<RegistryByteBuf, VfxPayload> CODEC = PacketCodec.of(VfxPayload::write, VfxPayload::read);

    private void write(RegistryByteBuf buf) {
        buf.writeByte(kind.ordinal());
        buf.writeByte(style.ordinal());
        buf.writeInt(color);
        buf.writeVarInt(entityId);
        writeVec(buf, origin);
        writeVec(buf, dir);
        buf.writeFloat(size);
        buf.writeFloat(extra);
    }

    private static VfxPayload read(RegistryByteBuf buf) {
        return new VfxPayload(VfxKind.byOrdinal(buf.readByte()), VfxStyle.byOrdinal(buf.readByte()), buf.readInt(), buf.readVarInt(),
                readVec(buf), readVec(buf), buf.readFloat(), buf.readFloat());
    }

    private static void writeVec(ByteBuf buf, Vec3d v) {
        buf.writeFloat((float) v.x);
        buf.writeFloat((float) v.y);
        buf.writeFloat((float) v.z);
    }

    private static Vec3d readVec(ByteBuf buf) {
        return new Vec3d(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
