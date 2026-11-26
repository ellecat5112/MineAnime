package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/** Spherical burst around (or in front of) the caster: Kaiten, Explosive Wave, Reiatsu release; {@code lift} launches targets. */
public record AoeSphereAction(float radius, float damage, float knockback, int fireTicks, float forwardOffset, int color, float lift)
        implements IAbilityAction {
    public AoeSphereAction(float radius, float damage, float knockback, int fireTicks, float forwardOffset, int color) {
        this(radius, damage, knockback, fireTicks, forwardOffset, color, 0f);
    }

    public static final MapCodec<AoeSphereAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.floatRange(0.5f, 32f).fieldOf("radius").forGetter(AoeSphereAction::radius),
            Codec.FLOAT.optionalFieldOf("damage", 4f).forGetter(AoeSphereAction::damage),
            Codec.FLOAT.optionalFieldOf("knockback", 0.5f).forGetter(AoeSphereAction::knockback),
            Codec.INT.optionalFieldOf("fire_ticks", 0).forGetter(AoeSphereAction::fireTicks),
            Codec.FLOAT.optionalFieldOf("forward_offset", 0f).forGetter(AoeSphereAction::forwardOffset),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(AoeSphereAction::color),
            Codec.floatRange(0f, 4f).optionalFieldOf("lift", 0f).forGetter(AoeSphereAction::lift)
    ).apply(i, AoeSphereAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.AOE_SPHERE; }

    @Override
    public void execute(AbilityContext ctx) {
        float r = radius * (float) Math.sqrt(ctx.scale());
        Vec3d look = ctx.look();
        Vec3d center = ctx.caster().getPos().add(look.x * forwardOffset, 0.9, look.z * forwardOffset);
        Box box = new Box(center, center).expand(r);
        for (LivingEntity target : ctx.world().getEntitiesByClass(LivingEntity.class, box,
                e -> e != ctx.caster() && e.isAlive() && e.squaredDistanceTo(center) <= r * r)) {
            if (ctx.damage(target, damage, fireTicks, knockback)) DashStrikeAction.launch(target, lift);
        }
        com.animecore.vfx.AbilityVfx.emit(ctx, com.animecore.vfx.VfxKind.SHOCKWAVE, center, look, r, 0f);
    }
}
