package com.animecore.ability.action;

import com.animecore.ability.AbilityContext;
import com.animecore.entity.EnergyProjectileEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

/**
 * Fires {@code count} projectiles. Without {@code entity}, spawns the engine's texture-free
 * {@link EnergyProjectileEntity} (Fireball Jutsu, Ki blast, Cero, Getsuga) tinted by {@code color}.
 */
public record ProjectileAction(
        Optional<EntityType<?>> entity,
        float speed,
        float damage,
        float size,
        float aoeRadius,
        int fireTicks,
        int count,
        float spreadDegrees,
        int lifetimeTicks,
        int color,
        Optional<Identifier> trailParticle,
        boolean sizeScalesWithCharge
) implements IAbilityAction {
    public static final MapCodec<ProjectileAction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Registries.ENTITY_TYPE.getCodec().optionalFieldOf("entity").forGetter(ProjectileAction::entity),
            Codec.FLOAT.optionalFieldOf("speed", 1.5f).forGetter(ProjectileAction::speed),
            Codec.FLOAT.optionalFieldOf("damage", 4f).forGetter(ProjectileAction::damage),
            Codec.floatRange(0.1f, 8f).optionalFieldOf("size", 0.5f).forGetter(ProjectileAction::size),
            Codec.floatRange(0f, 16f).optionalFieldOf("aoe_radius", 0f).forGetter(ProjectileAction::aoeRadius),
            Codec.INT.optionalFieldOf("fire_ticks", 0).forGetter(ProjectileAction::fireTicks),
            Codec.intRange(1, 32).optionalFieldOf("count", 1).forGetter(ProjectileAction::count),
            Codec.FLOAT.optionalFieldOf("spread_degrees", 0f).forGetter(ProjectileAction::spreadDegrees),
            Codec.intRange(1, 1200).optionalFieldOf("lifetime_ticks", 60).forGetter(ProjectileAction::lifetimeTicks),
            Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(ProjectileAction::color),
            Identifier.CODEC.optionalFieldOf("trail_particle").forGetter(ProjectileAction::trailParticle),
            Codec.BOOL.optionalFieldOf("size_scales_with_charge", true).forGetter(ProjectileAction::sizeScalesWithCharge)
    ).apply(i, ProjectileAction::new));

    @Override public AbilityActionType<?> type() { return AbilityActions.PROJECTILE; }

    @Override
    public void execute(AbilityContext ctx) {
        var caster = ctx.caster();
        float baseYaw = caster.getYaw();
        float pitch = caster.getPitch();
        Vec3d eye = ctx.eyePos();
        for (int n = 0; n < count; n++) {
            float yaw = baseYaw + spreadDegrees * (n - (count - 1) / 2f);
            Vec3d dir = Vec3d.fromPolar(pitch, yaw);
            Vec3d spawn = eye.add(dir.multiply(0.6)).subtract(0, 0.15, 0);
            if (entity.isPresent()) {
                Entity e = entity.get().create(ctx.world());
                if (e == null) continue;
                e.refreshPositionAndAngles(spawn.x, spawn.y, spawn.z, yaw, pitch);
                if (e instanceof ProjectileEntity projectile) projectile.setOwner(caster);
                e.setVelocity(dir.multiply(speed));
                ctx.world().spawnEntity(e);
            } else {
                float scaledSize = sizeScalesWithCharge ? size * ctx.scale() : size;
                EnergyProjectileEntity p = new EnergyProjectileEntity(ctx.world(), caster);
                p.configure(ctx.scaledDamage(damage), ctx.affinity(), aoeRadius * (sizeScalesWithCharge ? ctx.scale() : 1f),
                        fireTicks, lifetimeTicks, color, scaledSize, trailParticle.orElse(null));
                p.refreshPositionAndAngles(spawn.x, spawn.y, spawn.z, yaw, pitch);
                p.setVelocity(dir.multiply(speed));
                ctx.world().spawnEntity(p);
            }
        }
    }
}
