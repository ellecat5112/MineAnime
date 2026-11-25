package com.animecore.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

/**
 * Left-click profile. {@code damage}/{@code speed} follow vanilla attribute conventions (added to the base 1 damage /
 * 4 attacks per second); {@code reach} adds to entity interaction range (spears, staves, chains).
 */
public record AttackProfile(float damage, float speed, float reach, List<OnHitEffect> onHit) {
    public static final AttackProfile FIST = new AttackProfile(0f, 0f, 0f, List.of());
    public static final Codec<AttackProfile> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.optionalFieldOf("damage", 0f).forGetter(AttackProfile::damage),
            Codec.FLOAT.optionalFieldOf("speed", 0f).forGetter(AttackProfile::speed),
            Codec.FLOAT.optionalFieldOf("reach", 0f).forGetter(AttackProfile::reach),
            OnHitEffect.CODEC.listOf().optionalFieldOf("on_hit", List.of()).forGetter(AttackProfile::onHit)
    ).apply(i, AttackProfile::new));
}
