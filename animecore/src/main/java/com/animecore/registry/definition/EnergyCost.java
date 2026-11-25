package com.animecore.registry.definition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

/**
 * @param pool              energy pool id, e.g. {@code narutorpg:chakra}, {@code dragonballrpg:ki}
 * @param amount            consumed when the ability fires
 * @param chargeDrainPerTick consumed every tick while a CHARGED ability is held
 */
public record EnergyCost(Identifier pool, float amount, float chargeDrainPerTick) {
    public static final Codec<EnergyCost> CODEC = RecordCodecBuilder.create(i -> i.group(
            Identifier.CODEC.fieldOf("pool").forGetter(EnergyCost::pool),
            Codec.floatRange(0f, Float.MAX_VALUE).optionalFieldOf("amount", 0f).forGetter(EnergyCost::amount),
            Codec.floatRange(0f, Float.MAX_VALUE).optionalFieldOf("charge_drain_per_tick", 0f).forGetter(EnergyCost::chargeDrainPerTick)
    ).apply(i, EnergyCost::new));
}
