package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;

/** Summons entities around the caster: Summoning Jutsu, Shadow Clones (custom type), Shikigami, Saibamen. */
public record SummonEntityAction(EntityType<?> entity, int count, float radius, boolean persistent) implements IAbilityAction {
    public static final MapCodec<SummonEntityAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Registries.ENTITY_TYPE.getCodec().fieldOf("entity").forGetter(SummonEntityAction::entity),
            Codec.intRange(1, 16).optionalFieldOf("count", 1).forGetter(SummonEntityAction::count),
            Codec.floatRange(0f, 16f).optionalFieldOf("radius", 2f).forGetter(SummonEntityAction::radius),
            Codec.BOOL.optionalFieldOf("persistent", false).forGetter(SummonEntityAction::persistent)
    ).apply(i, SummonEntityAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.SUMMON_ENTITY; }

    @Override
    public void execute(AbilityContext ctx) {
        var caster = ctx.caster();
        for (int n = 0; n < count; n++) {
            Entity e = entity.create(ctx.world());
            if (e == null) return;
            double a = (Math.PI * 2 * n) / count;
            double x = caster.getX() + Math.cos(a) * radius;
            double z = caster.getZ() + Math.sin(a) * radius;
            e.refreshPositionAndAngles(x, caster.getY(), z, caster.getYaw(), 0f);
            if (e instanceof MobEntity mob && persistent) mob.setPersistent();
            ctx.world().spawnEntity(e);
            ctx.world().spawnParticles(ParticleTypes.POOF, x, caster.getY() + 0.5, z, 10, 0.3, 0.5, 0.3, 0.02);
            com.animecore.vfx.AbilityVfx.emit(ctx, com.animecore.vfx.VfxKind.IMPACT, new net.minecraft.util.math.Vec3d(x, caster.getY() + 0.5, z),
                    net.minecraft.util.math.Vec3d.ZERO, 1.2f, 0f);
        }
    }
}
