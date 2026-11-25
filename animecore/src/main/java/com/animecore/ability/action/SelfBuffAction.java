package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

/** Applies status effects / healing to the caster: Eight Gates, Kaioken burst, Blut Vene. */
public record SelfBuffAction(List<Effect> effects, float heal) implements IAbilityAction {
    public record Effect(RegistryEntry<StatusEffect> effect, int duration, int amplifier, boolean showParticles) {
        public static final Codec<Effect> CODEC = RecordCodecBuilder.create(i -> i.group(
                Registries.STATUS_EFFECT.getEntryCodec().fieldOf("effect").forGetter(Effect::effect),
                Codec.intRange(1, 1_000_000).optionalFieldOf("duration", 200).forGetter(Effect::duration),
                Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(Effect::amplifier),
                Codec.BOOL.optionalFieldOf("show_particles", false).forGetter(Effect::showParticles)
        ).apply(i, Effect::new));
    }

    public static final MapCodec<SelfBuffAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Effect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(SelfBuffAction::effects),
            Codec.FLOAT.optionalFieldOf("heal", 0f).forGetter(SelfBuffAction::heal)
    ).apply(i, SelfBuffAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.SELF_BUFF; }

    @Override
    public void execute(AbilityContext ctx) {
        for (Effect e : effects) {
            int duration = (int) (e.duration() * ctx.scale());
            ctx.caster().addStatusEffect(new StatusEffectInstance(e.effect(), duration, e.amplifier(), false, e.showParticles(), true));
        }
        if (heal > 0f) ctx.caster().heal(heal * ctx.scale());
    }
}
