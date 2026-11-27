package com.animecore.vfx;

import com.animecore.platform.AnimeNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.Vec3d;

/**
 * Combat feel for every anime mod: camera shake and impact flashes for heavy blows, and anime title cards (boss
 * intros, chapter lower-thirds, arc-end banners in the mod's {@link com.animecore.api.AnimeTheme}). Server API; the
 * client draws them ({@code com.animecore.client.vfx.JuiceClient}).
 */
public final class Juice {
    private Juice() {}

    /** Camera shake ({@code strength} 0..1, 1 = a road roller landing) and an optional full-screen flash. */
    public record ShakePayload(float strength, int ticks, int flashColor, float flash) implements CustomPayload {
        public static final Id<ShakePayload> ID = AnimeNetworking.payloadId("animecore", "shake");
        public static final PacketCodec<RegistryByteBuf, ShakePayload> CODEC = PacketCodec.tuple(
                PacketCodecs.FLOAT, ShakePayload::strength, PacketCodecs.VAR_INT, ShakePayload::ticks,
                PacketCodecs.INTEGER, ShakePayload::flashColor, PacketCodecs.FLOAT, ShakePayload::flash, ShakePayload::new);
        @Override public Id<? extends CustomPayload> getId() { return ID; }
    }

    public enum CardKind { BOSS, CHAPTER, ARC_END }

    /** A title card of {@code system}'s theme. */
    public record CardPayload(String system, int kind, Text title, Text subtitle, int ticks) implements CustomPayload {
        public static final Id<CardPayload> ID = AnimeNetworking.payloadId("animecore", "title_card");
        public static final PacketCodec<RegistryByteBuf, CardPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.STRING, CardPayload::system, PacketCodecs.VAR_INT, CardPayload::kind,
                TextCodecs.REGISTRY_PACKET_CODEC, CardPayload::title, TextCodecs.REGISTRY_PACKET_CODEC, CardPayload::subtitle,
                PacketCodecs.VAR_INT, CardPayload::ticks, CardPayload::new);
        @Override public Id<? extends CustomPayload> getId() { return ID; }
        public CardKind cardKind() { return CardKind.values()[Math.max(0, Math.min(kind, CardKind.values().length - 1))]; }
    }

    public static void init() {
        AnimeNetworking.registerS2C(ShakePayload.ID, ShakePayload.CODEC);
        AnimeNetworking.registerS2C(CardPayload.ID, CardPayload.CODEC);
        // getting hit hard shakes your screen
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, base, taken, blocked) -> {
            if (entity instanceof ServerPlayerEntity p && taken >= 3f) {
                shake(p, Math.min(1f, taken / Math.max(8f, p.getMaxHealth() * 0.6f)), 8);
            }
        });
    }

    public static void shake(ServerPlayerEntity player, float strength, int ticks) {
        AnimeNetworking.send(player, new ShakePayload(strength, ticks, 0, 0f));
    }

    /** Shake (with distance falloff) for every player within {@code radius} of {@code at}. */
    public static void shakeAround(ServerWorld world, Vec3d at, double radius, float strength, int ticks) {
        impact(world, at, radius, strength, ticks, 0, 0f);
    }

    /** Shake + flash ({@code flash} 0..1 opacity of {@code color}) around a heavy impact. */
    public static void impact(ServerWorld world, Vec3d at, double radius, float strength, int ticks, int color, float flash) {
        for (ServerPlayerEntity p : world.getPlayers()) {
            double d = p.getPos().distanceTo(at);
            if (d > radius) continue;
            float k = (float) (1.0 - d / radius * 0.7);
            AnimeNetworking.send(p, new ShakePayload(strength * k, ticks, color, flash * k));
        }
    }

    public static void card(ServerPlayerEntity player, String system, CardKind kind, Text title, Text subtitle, int ticks) {
        AnimeNetworking.send(player, new CardPayload(system, kind.ordinal(), title, subtitle, ticks));
    }
}
