package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.manifestation.DamageLinkManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;

/**
 * Lovers: the Stand burrows into the aimed creature's brain. For {@code duration_ticks} every hit the caster takes is
 * felt by the host instead (times {@code ratio}); see {@link DamageLinkManager}.
 */
public record DamageLinkAction(float range, int durationTicks, float ratio) implements IAbilityAction {
    public static final MapCodec<DamageLinkAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.floatRange(1f, 128f).optionalFieldOf("range", 24f).forGetter(DamageLinkAction::range),
            Codec.intRange(20, 12000).optionalFieldOf("duration_ticks", 400).forGetter(DamageLinkAction::durationTicks),
            Codec.floatRange(0f, 8f).optionalFieldOf("ratio", 1f).forGetter(DamageLinkAction::ratio)
    ).apply(i, DamageLinkAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.DAMAGE_LINK; }

    @Override
    public void execute(AbilityContext ctx) {
        LivingEntity host = RemoteStrikeAction.aim(ctx, range);
        if (host == null) return;
        DamageLinkManager.link(ctx.caster(), host, durationTicks, ratio);
        ctx.world().spawnParticles(ParticleTypes.HEART, host.getX(), host.getEyeY() + 0.3, host.getZ(), 6, 0.2, 0.2, 0.2, 0.0);
    }
}
