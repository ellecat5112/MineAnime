package com.animecore.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;

/** Extra behaviour when a tool's melee attack lands. */
public record OnHitEffect(Kind kind, float strength, int duration) {
    public enum Kind implements StringIdentifiable {
        POISON, BLEED, PARALYZE, FIRE, LIGHTNING, FREEZE, DRAIN, KNOCKBACK, PIERCE, BLIND, WEAKEN, SHIELD_BREAK;
        public static final Codec<Kind> CODEC = StringIdentifiable.createCodec(Kind::values);
        @Override public String asString() { return name().toLowerCase(java.util.Locale.ROOT); }
    }

    public static final Codec<OnHitEffect> CODEC = RecordCodecBuilder.create(i -> i.group(
            Kind.CODEC.fieldOf("kind").forGetter(OnHitEffect::kind),
            Codec.FLOAT.optionalFieldOf("strength", 1f).forGetter(OnHitEffect::strength),
            Codec.INT.optionalFieldOf("duration", 60).forGetter(OnHitEffect::duration)
    ).apply(i, OnHitEffect::new));
}
