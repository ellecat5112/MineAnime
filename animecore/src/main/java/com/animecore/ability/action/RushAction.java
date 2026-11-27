package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.entity.ManifestationEntity;
import com.animecore.manifestation.ManifestationService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

/**
 * A Stand rush ("ORA ORA ORA!", "MUDA MUDA MUDA!", Silver Chariot's thrusts): the caster's manifestation steps in front
 * and lands {@code hits} blows, one every {@code interval_ticks}, on everything within {@code radius} of a point
 * {@code reach} blocks ahead. The last blow sends targets flying. Works without a Stand too (the caster's own fists).
 */
public record RushAction(int hits, float damage, float reach, float radius, float knockback, int intervalTicks, Optional<Text> shout)
        implements IAbilityAction {
    public static final MapCodec<RushAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.intRange(1, 80).optionalFieldOf("hits", 12).forGetter(RushAction::hits),
            Codec.floatRange(0f, 200f).optionalFieldOf("damage", 1.5f).forGetter(RushAction::damage),
            Codec.floatRange(0.5f, 16f).optionalFieldOf("reach", 2.2f).forGetter(RushAction::reach),
            Codec.floatRange(0.3f, 8f).optionalFieldOf("radius", 1.6f).forGetter(RushAction::radius),
            Codec.floatRange(0f, 8f).optionalFieldOf("knockback", 1.6f).forGetter(RushAction::knockback),
            Codec.intRange(1, 10).optionalFieldOf("interval_ticks", 2).forGetter(RushAction::intervalTicks),
            TextCodecs.CODEC.optionalFieldOf("shout").forGetter(RushAction::shout)
    ).apply(i, RushAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.RUSH; }

    @Override
    public void execute(AbilityContext ctx) {
        var caster = ctx.caster();
        int duration = hits * intervalTicks + 1;
        ManifestationEntity stand = ManifestationService.standOf(caster);
        if (stand != null) stand.act(ManifestationEntity.State.RUSH, duration + 4);
        shout.ifPresent(s -> Shouts.shout(ctx, s));
        com.animecore.tool.ToolScheduler.run(duration, t -> {
            if (!caster.isAlive()) return false;
            if (t % intervalTicks != 0) return true;
            int n = t / intervalTicks;
            boolean last = n == hits - 1;
            Vec3d look = caster.getRotationVec(1f);
            Vec3d center = caster.getEyePos().add(look.multiply(reach * ctx.scale())).subtract(0, 0.4, 0);
            Box box = new Box(center, center).expand(radius * ctx.scale());
            for (LivingEntity target : ctx.world().getEntitiesByClass(LivingEntity.class, box,
                    e -> e != caster && e.isAlive() && !(e instanceof ManifestationEntity))) {
                ctx.damage(target, damage, 0, last ? knockback : 0.05f);
                if (last) target.addVelocity(look.x * knockback * 0.5, 0.25, look.z * knockback * 0.5);
            }
            double ox = (ctx.world().random.nextDouble() - 0.5) * radius, oy = (ctx.world().random.nextDouble() - 0.5) * radius;
            ctx.world().spawnParticles(last ? ParticleTypes.EXPLOSION : ParticleTypes.CRIT, center.x + ox, center.y + oy, center.z + ox,
                    last ? 1 : 4, 0.1, 0.1, 0.1, 0.25);
            if (n % 2 == 0) ctx.world().playSound(null, caster.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS,
                    0.6f, 0.9f + ctx.world().random.nextFloat() * 0.4f);
            return true;
        });
    }
}
