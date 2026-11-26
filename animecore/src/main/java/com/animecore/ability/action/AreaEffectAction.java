package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
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
                               float pull, int color, Targets targets) implements IAbilityAction {
    /** Who is affected: everyone but the caster (default), only allies (healing), or only foes. */
    public enum Targets implements net.minecraft.util.StringIdentifiable {
        OTHERS("others"), ALLIES("allies"), FOES("foes");
        public static final Codec<Targets> CODEC = net.minecraft.util.StringIdentifiable.createCodec(Targets::values);
        private final String name;
        Targets(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }

    public AreaEffectAction(float radius, float coneDegrees, List<SelfBuffAction.Effect> effects, float damage, float pull, int color) {
        this(radius, coneDegrees, effects, damage, pull, color, Targets.OTHERS);
    }

    /** Players, tame animals and non-hostile story characters count as allies. */
    static boolean ally(LivingEntity e) {
        if (e instanceof net.minecraft.entity.player.PlayerEntity) return true;
        if (e instanceof com.animecore.entity.StoryNpcEntity npc) return !npc.role().hostile();
        if (e instanceof net.minecraft.entity.passive.TameableEntity t) return t.isTamed();
        return !(e instanceof net.minecraft.entity.mob.Monster);
    }

    public static final MapCodec<AreaEffectAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.floatRange(0.5f, 48f).fieldOf("radius").forGetter(AreaEffectAction::radius),
            Codec.floatRange(1f, 360f).optionalFieldOf("cone_degrees", 360f).forGetter(AreaEffectAction::coneDegrees),
            SelfBuffAction.Effect.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(AreaEffectAction::effects),
            Codec.FLOAT.optionalFieldOf("damage", 0f).forGetter(AreaEffectAction::damage),
            Codec.floatRange(-4f, 4f).optionalFieldOf("pull", 0f).forGetter(AreaEffectAction::pull),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(AreaEffectAction::color),
            Targets.CODEC.optionalFieldOf("targets", Targets.OTHERS).forGetter(AreaEffectAction::targets)
    ).apply(i, AreaEffectAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.AREA_EFFECT; }

    @Override
    public void execute(AbilityContext ctx) {
        float r = radius * (float) Math.sqrt(ctx.scale());
        Vec3d origin = ctx.caster().getPos().add(0, 0.9, 0);
        Vec3d look = ctx.look();
        double cosHalf = Math.cos(Math.toRadians(coneDegrees / 2.0));
        Box box = new Box(origin, origin).expand(r);
        com.animecore.vfx.ScreenEffect screen = com.animecore.vfx.AbilityVfx.resolveScreen(ctx.definition(), ctx.vfxStyle(), this);
        for (LivingEntity target : ctx.world().getEntitiesByClass(LivingEntity.class, box,
                e -> e != ctx.caster() && e.isAlive() && e.squaredDistanceTo(origin) <= r * r
                        && (targets == Targets.OTHERS || ally(e) == (targets == Targets.ALLIES)))) {
            Vec3d to = target.getPos().add(0, target.getHeight() / 2, 0).subtract(origin);
            if (coneDegrees < 360f && to.lengthSquared() > 1e-4 && to.normalize().dotProduct(look) < cosHalf) continue;
            for (SelfBuffAction.Effect e : effects) {
                if (e.effect().value().isInstant()) {   // instant health / damage land now, not on the target's next tick
                    e.effect().value().applyInstantEffect(ctx.caster(), ctx.caster(), target, e.amplifier(), 1.0);
                    continue;
                }
                target.addStatusEffect(new StatusEffectInstance(e.effect(), (int) (e.duration() * ctx.scale()), e.amplifier(),
                        false, e.showParticles(), true), ctx.caster());
            }
            if (damage > 0f) ctx.damage(target, damage, 0, 0f);
            int dur = effects.stream().mapToInt(SelfBuffAction.Effect::duration).max().orElse(60);
            com.animecore.vfx.AbilityVfx.emit(ctx.world(), target, com.animecore.vfx.VfxKind.MARK, ctx.vfxStyle(), ctx.vfxColor(),
                    target.getPos().add(0, target.getHeight() + 0.4, 0), Vec3d.ZERO, Math.min(200, dur), target.getId());
            if (target instanceof net.minecraft.server.network.ServerPlayerEntity sp) {
                int ticks = Math.max(60, Math.min(200, dur));
                var profile = ctx.definition().visuals().illusion();
                if (profile.isPresent()) com.animecore.vfx.AbilityVfx.screen(sp, screen, ctx.vfxStyle(), ctx.vfxColor(), ticks, profile.get());
                else com.animecore.vfx.AbilityVfx.screen(sp, screen, ctx.vfxStyle(), ctx.vfxColor(), ticks);
            }
            if (pull != 0f && to.lengthSquared() > 1e-4) {
                Vec3d v = to.normalize().multiply(-pull * 0.5);
                target.addVelocity(v.x, 0.1 * Math.abs(pull), v.z);
                target.velocityModified = true;
            }
        }
        var illusion = ctx.definition().visuals().illusion();
        if (illusion.isPresent()) {
            var pr = illusion.get();
            com.animecore.vfx.AbilityVfx.emit(ctx.world(), ctx.caster(), com.animecore.vfx.VfxKind.ILLUSION, ctx.vfxStyle(), pr.color(), origin,
                    new Vec3d(pr.pattern().ordinal(), pr.count(), pr.speed()), r, pr.color2());
        } else {
            com.animecore.vfx.AbilityVfx.emit(ctx, com.animecore.vfx.VfxKind.AREA, origin, look, r, coneDegrees);
        }
    }
}
