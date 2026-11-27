package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.manifestation.TimeStopManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

import java.util.Optional;

/**
 * Stops time for {@code duration_ticks} within {@code radius} of the caster ("ZA WARUDO!"); see {@link TimeStopManager}.
 * The caster (and time-immune beings) keep moving; {@code shout} is shown to everyone nearby.
 */
public record TimeStopAction(int durationTicks, float radius, Optional<Text> shout) implements IAbilityAction {
    public static final MapCodec<TimeStopAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.intRange(1, 1200).optionalFieldOf("duration_ticks", 100).forGetter(TimeStopAction::durationTicks),
            Codec.floatRange(1f, 256f).optionalFieldOf("radius", 48f).forGetter(TimeStopAction::radius),
            TextCodecs.CODEC.optionalFieldOf("shout").forGetter(TimeStopAction::shout)
    ).apply(i, TimeStopAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.TIME_STOP; }

    @Override
    public void execute(AbilityContext ctx) {
        shout.ifPresent(s -> Shouts.shout(ctx, s));
        TimeStopManager.stop(ctx.caster(), radius, durationTicks);
    }
}
