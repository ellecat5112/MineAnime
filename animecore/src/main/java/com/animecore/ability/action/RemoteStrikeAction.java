package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.entity.ManifestationEntity;
import com.animecore.manifestation.ManifestationService;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A remote Stand attack: the manifestation flies out (at {@code speed} blocks per tick, or instantly with
 * {@code teleport} - Hanged Man travelling through reflections) to the creature the caster aims at within {@code range},
 * strikes it ({@code damage}, {@code effects}, {@code fire_ticks}) and comes back. Tower of Gray's Tower Needle,
 * Ebony Devil, Hierophant Green's tentacles, Geb's water claw...
 */
public record RemoteStrikeAction(float range, float damage, float speed, boolean teleport, int fireTicks, float radius,
                                 List<SelfBuffAction.Effect> effects) implements IAbilityAction {
    public static final MapCodec<RemoteStrikeAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.floatRange(1f, 256f).optionalFieldOf("range", 16f).forGetter(RemoteStrikeAction::range),
            Codec.floatRange(0f, 200f).optionalFieldOf("damage", 6f).forGetter(RemoteStrikeAction::damage),
            Codec.floatRange(0.2f, 16f).optionalFieldOf("speed", 1.5f).forGetter(RemoteStrikeAction::speed),
            Codec.BOOL.optionalFieldOf("teleport", false).forGetter(RemoteStrikeAction::teleport),
            Codec.intRange(0, 600).optionalFieldOf("fire_ticks", 0).forGetter(RemoteStrikeAction::fireTicks),
            Codec.floatRange(0f, 8f).optionalFieldOf("radius", 0f).forGetter(RemoteStrikeAction::radius),
            SelfBuffAction.Effect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(RemoteStrikeAction::effects)
    ).apply(i, RemoteStrikeAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.REMOTE_STRIKE; }

    @Override
    public void execute(AbilityContext ctx) {
        var caster = ctx.caster();
        LivingEntity target = aim(ctx);
        Vec3d end = target != null ? target.getPos().add(0, target.getHeight() * 0.6, 0) : blockEnd(ctx);
        Vec3d start = ManifestationService.origin(caster);
        ManifestationEntity stand = ManifestationService.standOf(caster);
        int travel = teleport ? 1 : Math.max(1, (int) Math.ceil(start.distanceTo(end) / speed));
        if (stand != null) stand.strikeAt(end, travel + 8);
        Vec3d dir = end.subtract(start);
        com.animecore.tool.ToolScheduler.run(travel + 1, t -> {
            if (!caster.isAlive()) return false;
            Vec3d p = start.add(dir.multiply(Math.min(1.0, (t + 1) / (double) travel)));
            if (!teleport) ctx.world().spawnParticles(ParticleTypes.ENCHANTED_HIT, p.x, p.y, p.z, 2, 0.05, 0.05, 0.05, 0.0);
            if (t + 1 < travel) return true;
            Vec3d hitAt = target != null && target.isAlive() ? target.getPos().add(0, target.getHeight() * 0.6, 0) : end;
            if (stand != null && target != null) stand.strikeAt(hitAt, 8);
            for (LivingEntity victim : victims(ctx, target, hitAt)) {
                ctx.damage(victim, damage, fireTicks, 0.5f);
                for (SelfBuffAction.Effect e : effects) victim.addStatusEffect(new StatusEffectInstance(e.effect(), e.duration(), e.amplifier()), caster);
            }
            ctx.world().spawnParticles(ParticleTypes.SWEEP_ATTACK, hitAt.x, hitAt.y, hitAt.z, 1, 0, 0, 0, 0);
            ctx.world().spawnParticles(ParticleTypes.CRIT, hitAt.x, hitAt.y, hitAt.z, 10, 0.3, 0.3, 0.3, 0.3);
            return false;
        });
    }

    private List<LivingEntity> victims(AbilityContext ctx, @Nullable LivingEntity target, Vec3d at) {
        if (radius <= 0f) return target != null && target.isAlive() ? List.of(target) : List.of();
        return ctx.world().getEntitiesByClass(LivingEntity.class, new Box(at, at).expand(radius),
                e -> e != ctx.caster() && e.isAlive() && !(e instanceof ManifestationEntity));
    }

    /** The living thing closest to the caster's crosshair (within a narrow cone) and in sight. */
    static @Nullable LivingEntity aim(AbilityContext ctx, float range) {
        var caster = ctx.caster();
        if (caster instanceof net.minecraft.entity.mob.MobEntity mob && mob.getTarget() != null && mob.getTarget().squaredDistanceTo(mob) <= range * range) {
            return mob.getTarget();
        }
        Vec3d eye = caster.getEyePos(), look = caster.getRotationVec(1f);
        LivingEntity best = null;
        double bestScore = Double.MAX_VALUE;
        for (LivingEntity e : ctx.world().getEntitiesByClass(LivingEntity.class, caster.getBoundingBox().expand(range),
                e -> e != caster && e.isAlive() && !(e instanceof ManifestationEntity) && !e.isSpectator())) {
            Vec3d to = e.getPos().add(0, e.getHeight() * 0.5, 0).subtract(eye);
            double dist = to.length();
            if (dist > range) continue;
            double cos = to.normalize().dotProduct(look);
            if (cos < 0.93) continue;
            if (!caster.canSee(e)) continue;
            double score = dist * (1.6 - cos);
            if (score < bestScore) { bestScore = score; best = e; }
        }
        return best;
    }

    private @Nullable LivingEntity aim(AbilityContext ctx) { return aim(ctx, range); }

    private Vec3d blockEnd(AbilityContext ctx) {
        Entity caster = ctx.caster();
        Vec3d eye = caster.getEyePos(), to = eye.add(caster.getRotationVec(1f).multiply(range));
        BlockHitResult hit = ctx.world().raycast(new RaycastContext(eye, to, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, caster));
        return hit.getType() == HitResult.Type.MISS ? to : hit.getPos();
    }
}
