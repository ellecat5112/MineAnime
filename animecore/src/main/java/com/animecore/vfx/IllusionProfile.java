package com.animecore.vfx;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;
import java.util.Optional;

/**
 * Per-technique genjutsu vision ({@code visuals.illusion}): what a victim sees when caught. Every parameter changes the
 * procedural overlay, so each illusion can look unique:
 * <pre>{ "pattern": "crows", "color": 1052688, "color2": 12976128, "count": 7, "speed": 1.3, "density": 0.8,
 *   "particle": "minecraft:squid_ink" }</pre>
 */
public record IllusionProfile(Pattern pattern, int color, int color2, int count, float speed, float density, Optional<Identifier> particle) {

    public enum Pattern implements StringIdentifiable {
        SPIRAL, BLOOD_MOON, CROWS, PETALS, SHARDS, FOG, CHAINS, EYES, TENDRILS, FLAMES, STARS, WEB, RINGS, RAIN, VORTEX,
        LOTUS, SERPENTS, VEINS, KALEIDOSCOPE, SNOW;
        public static final Codec<Pattern> CODEC = StringIdentifiable.createCodec(Pattern::values);
        @Override public String asString() { return name().toLowerCase(Locale.ROOT); }
        public static Pattern byOrdinal(int i) { Pattern[] v = values(); return i >= 0 && i < v.length ? v[i] : SPIRAL; }
    }

    public static final IllusionProfile DEFAULT = new IllusionProfile(Pattern.SPIRAL, 0xC2185B, 0x4A148C, 3, 1f, 1f, Optional.empty());

    public static final Codec<IllusionProfile> CODEC = RecordCodecBuilder.create(i -> i.group(
            Pattern.CODEC.optionalFieldOf("pattern", Pattern.SPIRAL).forGetter(IllusionProfile::pattern),
            Codec.INT.optionalFieldOf("color", 0xC2185B).forGetter(IllusionProfile::color),
            Codec.INT.optionalFieldOf("color2", 0x4A148C).forGetter(IllusionProfile::color2),
            Codec.intRange(1, 16).optionalFieldOf("count", 3).forGetter(IllusionProfile::count),
            Codec.floatRange(0.1f, 4f).optionalFieldOf("speed", 1f).forGetter(IllusionProfile::speed),
            Codec.floatRange(0.1f, 2f).optionalFieldOf("density", 1f).forGetter(IllusionProfile::density),
            Identifier.CODEC.optionalFieldOf("particle").forGetter(IllusionProfile::particle)
    ).apply(i, IllusionProfile::new));

    public static final PacketCodec<ByteBuf, IllusionProfile> PACKET_CODEC = PacketCodec.of(
            (p, buf) -> {
                buf.writeByte(p.pattern.ordinal());
                buf.writeInt(p.color);
                buf.writeInt(p.color2);
                buf.writeByte(p.count);
                buf.writeFloat(p.speed);
                buf.writeFloat(p.density);
                PacketCodecs.optional(Identifier.PACKET_CODEC).encode(buf, p.particle);
            },
            buf -> new IllusionProfile(Pattern.byOrdinal(buf.readByte()), buf.readInt(), buf.readInt(), buf.readByte(), buf.readFloat(),
                    buf.readFloat(), PacketCodecs.optional(Identifier.PACKET_CODEC).decode(buf)));
}
