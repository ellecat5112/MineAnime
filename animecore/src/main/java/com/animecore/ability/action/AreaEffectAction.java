package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.platform.AnimeParticles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Status effects (and optional damage / pull) on every living target in a sphere or forward cone: genjutsu,
 * shadow binding, insect swarms, sensory pulses, poison mist. {@code cone_degrees} 360 = full sphere;
 * {@code pull} > 0 drags targets toward the caster, < 0 pushes them away.
 */
public record AreaEffectAction(float radius, float coneDegrees, List<SelfBuffAction.Effect> effects, float damage,
                               float pull, int color) implements IAbilityAction {
    public static final MapCodec<AreaEffectAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.floatRange(0.5f, 48f).fieldOf("radius").forGetter(AreaEffectAction::radius),
            Codec.floatRange(1f, 360f).optionalFieldOf("cone_degrees", 360f).forGetter(AreaEffectAction::coneDegrees),
            SelfBuffAction.Effect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(AreaEffectAction::effects),
            Codec.FLOAT.optionalFieldOf("damage", 0f).forGetter(AreaEffectAction::damage),
            Codec.floatRange(-4f, 4f).optionalFieldOf("pull", 0f).forGetter(AreaEffectAction::pull),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(AreaEffectAction::color)
    ).apply(i, AreaEffectAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.AREA_EFFECT; }

    @Override
    public void execute(AbilityContext ctx) {
        float r = radius * (float) Math.sqrt(ctx.scale());
        Vec3d origin = ctx.caster().getPos().add(0, 0.9, 0);
        Vec3d look = ctx.look();
        double cosHalf = Math.cos(Math.toRadians(coneDegrees / 2.0));
        Box box = new Box(origin, origin).expand(r);
        for (LivingEntity target : ctx.world().getEntitiesByClass(LivingEntity.class, box,
                e -> e != ctx.caster() && e.isAlive() && e.squaredDistanceTo(origin) <= r * r)) {
            Vec3d to = target.getPos().add(0, target.getHeight() / 2, 0).subtract(origin);
            if (coneDegrees < 360f && to.lengthSquared() > 1e-4 && to.normalize().dotProduct(look) < cosHalf) continue;
            for (SelfBuffAction.Effect e : effects) {
                target.addStatusEffect(new StatusEffectInstance(e.effect(), (int) (e.duration() * ctx.scale()), e.amplifier(),
                        false, e.showParticles(), true), ctx.caster());
            }
            if (damage > 0f) ctx.damage(target, damage, 0, 0f);
            if (pull != 0f && to.lengthSquared() > 1e-4) {
                Vec3d v = to.normalize().multiply(-pull * 0.5);
                target.addVelocity(v.x, 0.1 * Math.abs(pull), v.z);
                target.velocityModified = true;
            }
        }
        int points = (int) (r * 10);
        for (int n = 0; n < points; n++) {
            double a = (Math.PI * 2 * n) / points;
            Vec3d dir = new Vec3d(Math.cos(a), 0, Math.sin(a));
            if (coneDegrees < 360f && dir.dotProduct(new Vec3d(look.x, 0, look.z).normalize()) < cosHalf) continue;
            ctx.world().spawnParticles(AnimeParticles.dust(color, 1.2f), origin.x + dir.x * r, origin.y - 0.6, origin.z + dir.z * r, 1, 0, 0.3, 0, 0);
        }
    }
}
