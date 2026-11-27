package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.entity.ManifestationEntity;
import com.animecore.manifestation.CharmManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Mind control (Justice's fog puppets, Anubis possessing its wielder, Khnum's deceit): up to {@code max_targets} mobs
 * within {@code radius} / {@code cone_degrees} fight for the caster for {@code duration_ticks} ({@link CharmManager}).
 * Players cannot be puppeted; they stagger instead (slowness, nausea, weakness).
 */
public record CharmAction(float radius, float coneDegrees, int durationTicks, int maxTargets, float damage) implements IAbilityAction {
    public static final MapCodec<CharmAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.floatRange(0.5f, 64f).optionalFieldOf("radius", 8f).forGetter(CharmAction::radius),
            Codec.floatRange(1f, 360f).optionalFieldOf("cone_degrees", 360f).forGetter(CharmAction::coneDegrees),
            Codec.intRange(1, 6000).optionalFieldOf("duration_ticks", 300).forGetter(CharmAction::durationTicks),
            Codec.intRange(1, 64).optionalFieldOf("max_targets", 4).forGetter(CharmAction::maxTargets),
            Codec.floatRange(0f, 200f).optionalFieldOf("damage", 0f).forGetter(CharmAction::damage)
    ).apply(i, CharmAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.CHARM; }

    @Override
    public void execute(AbilityContext ctx) {
        var caster = ctx.caster();
        Vec3d origin = caster.getPos().add(0, 0.9, 0), look = ctx.look();
        double cosHalf = Math.cos(Math.toRadians(coneDegrees / 2.0));
        int n = 0;
        for (LivingEntity e : ctx.world().getEntitiesByClass(LivingEntity.class, new Box(origin, origin).expand(radius),
                t -> t != caster && t.isAlive() && !(t instanceof ManifestationEntity) && t.squaredDistanceTo(origin) <= radius * radius)) {
            Vec3d to = e.getPos().add(0, e.getHeight() / 2, 0).subtract(origin);
            if (coneDegrees < 360f && to.lengthSquared() > 1e-4 && to.normalize().dotProduct(look) < cosHalf) continue;
            if (damage > 0f) ctx.damage(e, damage, 0, 0f);
            if (e instanceof MobEntity mob && !(e instanceof com.animecore.entity.StoryNpcEntity npc && npc.role() == com.animecore.quest.StoryNpcDefinition.Role.BOSS)) {
                CharmManager.charm(mob, caster, durationTicks);
            } else if (e instanceof PlayerEntity || e instanceof com.animecore.entity.StoryNpcEntity) {
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, Math.min(durationTicks, 100), 2), caster);
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, Math.min(durationTicks, 140), 0), caster);
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, Math.min(durationTicks, 140), 1), caster);
            }
            ctx.world().spawnParticles(ParticleTypes.WITCH, e.getX(), e.getBodyY(0.8), e.getZ(), 12, 0.3, 0.4, 0.3, 0.02);
            if (++n >= maxTargets) break;
        }
    }
}
