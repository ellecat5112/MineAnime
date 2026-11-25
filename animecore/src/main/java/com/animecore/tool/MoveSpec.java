package com.animecore.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * One parameterised move. {@code params} is a small numeric bag (damage, speed, count, radius...) so 200+ tools can
 * be described compactly in JSON; {@code variant} selects sub-behaviour (e.g. impact "cloud", puppet "flame").
 */
public record MoveSpec(MoveType type, float cost, int cooldown, Map<String, Float> params, List<ToolEffect> effects,
                       int color, Optional<String> variant) {
    public static final Codec<MoveSpec> CODEC = RecordCodecBuilder.create(i -> i.group(
            MoveType.CODEC.fieldOf("type").forGetter(MoveSpec::type),
            Codec.FLOAT.optionalFieldOf("cost", 0f).forGetter(MoveSpec::cost),
            Codec.INT.optionalFieldOf("cooldown", 20).forGetter(MoveSpec::cooldown),
            Codec.unboundedMap(Codec.STRING, Codec.FLOAT).optionalFieldOf("params", Map.of()).forGetter(MoveSpec::params),
            ToolEffect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(MoveSpec::effects),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(MoveSpec::color),
            Codec.STRING.optionalFieldOf("variant").forGetter(MoveSpec::variant)
    ).apply(i, MoveSpec::new));

    public float f(String key, float fallback) {
        Float v = params.get(key);
        return v == null ? fallback : v;
    }

    public int i(String key, int fallback) {
        Float v = params.get(key);
        return v == null ? fallback : Math.round(v);
    }

    public boolean flag(String key) {
        return f(key, 0f) != 0f;
    }

    public String variantOr(String fallback) {
        return variant.orElse(fallback);
    }
}
