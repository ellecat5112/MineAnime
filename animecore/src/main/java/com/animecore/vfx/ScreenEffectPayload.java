package com.animecore.vfx;

import com.animecore.platform.AnimeNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

/** Tells one player to show a full-screen effect (genjutsu vision, darkness, flash) for {@code ticks}. */
public record ScreenEffectPayload(ScreenEffect effect, VfxStyle style, int color, int ticks, IllusionProfile profile) implements CustomPayload {
    public ScreenEffectPayload(ScreenEffect effect, VfxStyle style, int color, int ticks) {
        this(effect, style, color, ticks, new IllusionProfile(IllusionProfile.Pattern.SPIRAL, color, IllusionProfile.DEFAULT.color2(), 3, 1f, 1f,
                java.util.Optional.empty()));
    }

    public static final Id<ScreenEffectPayload> ID = AnimeNetworking.payloadId("animecore", "screen_effect");
    public static final PacketCodec<RegistryByteBuf, ScreenEffectPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT.xmap(ScreenEffect::byOrdinal, ScreenEffect::ordinal), ScreenEffectPayload::effect,
            PacketCodecs.VAR_INT.xmap(VfxStyle::byOrdinal, VfxStyle::ordinal), ScreenEffectPayload::style,
            PacketCodecs.INTEGER, ScreenEffectPayload::color,
            PacketCodecs.VAR_INT, ScreenEffectPayload::ticks,
            IllusionProfile.PACKET_CODEC, ScreenEffectPayload::profile,
            ScreenEffectPayload::new);

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
