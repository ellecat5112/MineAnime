package com.animecore.vfx;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

/**
 * Optional per-ability look override ({@code "visuals"} in ability JSON). Everything is optional: an ability without
 * it is animated from its affinity and action types.
 * <pre>{ "style": "illusion", "screen_effect": "illusion", "color": 12976128, "scale": 1.3 }</pre>
 */
public record AbilityVisuals(Optional<VfxStyle> style, ScreenEffect screenEffect, Optional<Integer> color, float scale) {
    public static final AbilityVisuals AUTO = new AbilityVisuals(Optional.empty(), ScreenEffect.AUTO, Optional.empty(), 1f);

    public static final Codec<AbilityVisuals> CODEC = RecordCodecBuilder.create(i -> i.group(
            VfxStyle.CODEC.optionalFieldOf("style").forGetter(AbilityVisuals::style),
            ScreenEffect.CODEC.optionalFieldOf("screen_effect", ScreenEffect.AUTO).forGetter(AbilityVisuals::screenEffect),
            Codec.INT.optionalFieldOf("color").forGetter(AbilityVisuals::color),
            Codec.floatRange(0.25f, 4f).optionalFieldOf("scale", 1f).forGetter(AbilityVisuals::scale)
    ).apply(i, AbilityVisuals::new));

    public static AbilityVisuals of(VfxStyle style) {
        return new AbilityVisuals(Optional.of(style), ScreenEffect.AUTO, Optional.empty(), 1f);
    }
}
