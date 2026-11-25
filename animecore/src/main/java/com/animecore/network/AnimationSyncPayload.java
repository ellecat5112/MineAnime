package com.animecore.network;

import com.animecore.animation.AnimLayer;
import com.animecore.platform.AnimeNetworking;
import com.animecore.platform.AnimePacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Optional;

/** S2C: play/stop a PlayerAnimator animation on one layer of one player. Empty animation = stop (with fade). */
public record AnimationSyncPayload(int entityId, AnimLayer layer, Optional<Identifier> animation, int fadeTicks,
                                   float speed, boolean upperBodyOnly) implements CustomPayload {
    public static final Id<AnimationSyncPayload> ID = AnimeNetworking.payloadId("animecore", "anim_sync");
    public static final PacketCodec<RegistryByteBuf, AnimationSyncPayload> CODEC = PacketCodec.tuple(
            AnimePacketCodecs.VAR_INT, AnimationSyncPayload::entityId,
            AnimePacketCodecs.enumCodec(AnimLayer.class), AnimationSyncPayload::layer,
            AnimePacketCodecs.OPTIONAL_IDENTIFIER, AnimationSyncPayload::animation,
            AnimePacketCodecs.VAR_INT, AnimationSyncPayload::fadeTicks,
            AnimePacketCodecs.FLOAT, AnimationSyncPayload::speed,
            AnimePacketCodecs.BOOL, AnimationSyncPayload::upperBodyOnly,
            AnimationSyncPayload::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
