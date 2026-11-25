package com.animecore.network;

import com.animecore.movement.MovementMode;
import com.animecore.platform.AnimeNetworking;
import com.animecore.platform.AnimePacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import org.joml.Vector3f;

/** Mobility & stage payloads (Phase 2). */
public final class MovementPayloads {
    private MovementPayloads() {}

    /** C2S: toggle a continuous movement mode (adhesion, flight). */
    public record Toggle(String system, MovementMode mode) implements CustomPayload {
        public static final Id<Toggle> ID = AnimeNetworking.payloadId("animecore", "movement_toggle");
        public static final PacketCodec<RegistryByteBuf, Toggle> CODEC = PacketCodec.tuple(
                AnimePacketCodecs.STRING, Toggle::system,
                AnimePacketCodecs.enumCodec(MovementMode.class), Toggle::mode,
                Toggle::new);
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }

    /** C2S: burst step in the direction of current movement input (forward/strafe in -1..1). */
    public record BurstStep(String system, float forward, float strafe) implements CustomPayload {
        public static final Id<BurstStep> ID = AnimeNetworking.payloadId("animecore", "burst_step");
        public static final PacketCodec<RegistryByteBuf, BurstStep> CODEC = PacketCodec.tuple(
                AnimePacketCodecs.STRING, BurstStep::system,
                AnimePacketCodecs.FLOAT, BurstStep::forward,
                AnimePacketCodecs.FLOAT, BurstStep::strafe,
                BurstStep::new);
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }

    /** C2S: step the active transformation up (+1, e.g. SSJ1 -> SSJ2) or down (-1) without reverting. */
    public record TransformStage(String system, int direction) implements CustomPayload {
        public static final Id<TransformStage> ID = AnimeNetworking.payloadId("animecore", "transform_stage");
        public static final PacketCodec<RegistryByteBuf, TransformStage> CODEC = PacketCodec.tuple(
                AnimePacketCodecs.STRING, TransformStage::system,
                AnimePacketCodecs.VAR_INT, TransformStage::direction,
                TransformStage::new);
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }

    /** S2C: draw a procedural afterimage trail between two points (burst step / dash). */
    public record Afterimage(int entityId, Vector3f from, Vector3f to, int color) implements CustomPayload {
        public static final Id<Afterimage> ID = AnimeNetworking.payloadId("animecore", "afterimage");
        public static final PacketCodec<RegistryByteBuf, Afterimage> CODEC = PacketCodec.tuple(
                AnimePacketCodecs.VAR_INT, Afterimage::entityId,
                PacketCodecs.VECTOR3F, Afterimage::from,
                PacketCodecs.VECTOR3F, Afterimage::to,
                PacketCodecs.INTEGER, Afterimage::color,
                Afterimage::new);
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }
}
