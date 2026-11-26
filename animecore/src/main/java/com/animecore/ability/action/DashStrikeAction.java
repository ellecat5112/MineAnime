package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.movement.MotionUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Short-range blink with optional strike along the path.
 * {@code behind_attacker} powers reactive dodges (Substitution, Z-Vanish, Flash Step counter).
 */
public record DashStrikeAction(Mode mode, float distance, float damage, float hitRadius, float knockback)
        implements IAbilityAction {
    public enum Mode implements StringIdentifiable {
        LOOK("look"), BACKWARD("backward"), BEHIND_ATTACKER("behind_attacker");
        public static final Codec<Mode> CODEC = StringIdentifiable.createCodec(Mode::values);
        private final String name;
        Mode(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }

    public static final MapCodec<DashStrikeAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Mode.CODEC.optionalFieldOf("mode", Mode.LOOK).forGetter(DashStrikeAction::mode),
            Codec.floatRange(0.5f, 64f).fieldOf("distance").forGetter(DashStrikeAction::distance),
            Codec.FLOAT.optionalFieldOf("damage", 0f).forGetter(DashStrikeAction::damage),
            Codec.floatRange(0.1f, 8f).optionalFieldOf("hit_radius", 1f).forGetter(DashStrikeAction::hitRadius),
            Codec.FLOAT.optionalFieldOf("knockback", 0.6f).forGetter(DashStrikeAction::knockback)
    ).apply(i, DashStrikeAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.DASH_STRIKE; }

    @Override
    public void execute(AbilityContext ctx) {
        var caster = ctx.caster();
        Vec3d start = caster.getPos();
        float yaw = caster.getYaw();
        Vec3d destination;

        Entity attacker = ctx.reactiveAttacker().orElse(null);
        if (mode == Mode.BEHIND_ATTACKER && attacker != null) {
            Vec3d facing = Vec3d.fromPolar(0, attacker.getYaw());
            destination = clip(ctx, attacker.getPos().add(0, 0.1, 0), attacker.getPos().subtract(facing.multiply(Math.min(distance, 2.5))));
            Vec3d toAttacker = attacker.getPos().subtract(destination);
            yaw = (float) (MathHelper.atan2(toAttacker.z, toAttacker.x) * MathHelper.DEGREES_PER_RADIAN) - 90f;
        } else {
            Vec3d dir = Vec3d.fromPolar(mode == Mode.LOOK ? caster.getPitch() : 0, caster.getYaw());
            if (mode != Mode.LOOK) dir = dir.multiply(-1);
            destination = clip(ctx, start.add(0, 0.1, 0), start.add(dir.multiply(distance * ctx.scale())));
        }

        Vec3d travel = destination.subtract(start);
        com.animecore.vfx.AbilityVfx.emit(ctx, com.animecore.vfx.VfxKind.DASH, start.add(0, 1, 0),
                travel.lengthSquared() > 1e-4 ? travel.normalize() : Vec3d.ZERO, (float) travel.length(), damage > 0f ? 1f : 0f);
        if (damage > 0f) {
            Box path = new Box(start, destination).expand(hitRadius).stretch(0, caster.getHeight(), 0);
            for (LivingEntity target : ctx.world().getEntitiesByClass(LivingEntity.class, path, e -> e != caster && e.isAlive())) {
                ctx.damage(target, damage, 0, knockback);
            }
        }
        caster.networkHandler.requestTeleport(destination.x, destination.y, destination.z, yaw, caster.getPitch());
        caster.fallDistance = 0f;
    }

    private static Vec3d clip(AbilityContext ctx, Vec3d from, Vec3d to) {
        return MotionUtil.clipDestination(ctx.world(), ctx.caster(), from, to);
    }
}
