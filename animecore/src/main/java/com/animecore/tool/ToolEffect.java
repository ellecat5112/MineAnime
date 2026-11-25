package com.animecore.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

/** A status effect applied by a tool move (cloud, consumable, buff, debuff). */
public record ToolEffect(RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
    public static final Codec<ToolEffect> CODEC = RecordCodecBuilder.create(i -> i.group(
            Registries.STATUS_EFFECT.getEntryCodec().fieldOf("id").forGetter(ToolEffect::effect),
            Codec.intRange(1, 1_000_000).optionalFieldOf("duration", 100).forGetter(ToolEffect::duration),
            Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(ToolEffect::amplifier)
    ).apply(i, ToolEffect::new));

    public StatusEffectInstance instance() {
        return new StatusEffectInstance(effect, duration, amplifier);
    }

    public void apply(LivingEntity target) {
        target.addStatusEffect(instance());
    }
}
