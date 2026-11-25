package com.animecore.tool;

import com.animecore.entity.ThrownToolEntity;
import com.animecore.platform.AnimeParticles;
import com.animecore.ward.ChakraWallBlock;
import com.animecore.ward.WardSystem;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/** What happens when a thrown tool lands. */
public final class ToolImpacts {
    private ToolImpacts() {}

    public static void apply(ThrownToolEntity proj, ServerWorld world, Vec3d at, @Nullable Entity direct) {
        Entity owner = proj.getOwner();
        float r = Math.max(1f, proj.radius);
        switch (proj.impact) {
            case "explode" -> world.createExplosion(proj, at.x, at.y, at.z, Math.max(1.5f, r * 0.8f), World.ExplosionSourceType.NONE);
            case "explode_water" -> {
                boolean wet = world.getFluidState(BlockPos.ofFloored(at)).isIn(net.minecraft.registry.tag.FluidTags.WATER)
                        || world.hasRain(BlockPos.ofFloored(at)) || direct instanceof LivingEntity l && l.isWet();
                world.createExplosion(proj, at.x, at.y, at.z, wet ? r * 1.6f : r * 0.6f, World.ExplosionSourceType.NONE);
            }
            case "bubble" -> {
                world.spawnParticles(ParticleTypes.BUBBLE_POP, at.x, at.y, at.z, 12, 0.3, 0.3, 0.3, 0.05);
                world.createExplosion(proj, at.x, at.y, at.z, 1.2f, World.ExplosionSourceType.NONE);
            }
            case "cloud", "flash" -> {
                cloud(world, at, r, proj.effects, proj.tint() == -1 ? 0xCCCCCC : proj.tint(), owner);
                if (proj.impact.equals("flash")) {
                    world.spawnParticles(ParticleTypes.FLASH, at.x, at.y + 0.5, at.z, 1, 0, 0, 0, 0);
                    for (LivingEntity e : ToolTargeting.sphere(proj, at, r * 2)) {
                        if (e != owner) for (ToolEffect fx : proj.effects) fx.apply(e);
                    }
                    world.playSound(null, at.x, at.y, at.z, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 1f, 1.6f);
                }
            }
            case "sonic" -> {
                for (LivingEntity e : ToolTargeting.sphere(proj, at, r * 2)) if (e != owner) for (ToolEffect fx : proj.effects) fx.apply(e);
                world.playSound(null, at.x, at.y, at.z, SoundEvents.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1.5f, 1f);
            }
            case "ice" -> {
                for (LivingEntity e : ToolTargeting.sphere(proj, at, r)) {
                    if (e == owner) continue;
                    e.damage(world.getDamageSources().freeze(), proj.damage);
                    e.setFrozenTicks(Math.max(e.getFrozenTicks(), 160));
                    e.addVelocity(0, 0.5, 0);
                    for (ToolEffect fx : proj.effects) fx.apply(e);
                }
                world.spawnParticles(ParticleTypes.SNOWFLAKE, at.x, at.y + 0.5, at.z, 60, r / 2, 0.8, r / 2, 0.05);
                world.playSound(null, at.x, at.y, at.z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS, 1f, 0.6f);
            }
            case "spikes" -> {
                for (int k = 0; k < 12; k++) {
                    double a = k * Math.PI / 6;
                    ThrownToolEntity spike = new ThrownToolEntity(world, owner instanceof LivingEntity l ? l : null, new ItemStack(Items.IRON_NUGGET));
                    spike.setPosition(at.x, at.y + 0.3, at.z);
                    spike.damage = Math.max(2f, proj.damage);
                    spike.setVelocity(Math.cos(a) * 0.9, 0.15, Math.sin(a) * 0.9);
                    world.spawnEntity(spike);
                }
                world.playSound(null, at.x, at.y, at.z, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundCategory.PLAYERS, 0.6f, 1.6f);
            }
            case "fire" -> {
                for (LivingEntity e : ToolTargeting.sphere(proj, at, r)) {
                    if (e == owner) continue;
                    e.setOnFireForTicks(100);
                    e.damage(world.getDamageSources().onFire(), proj.damage);
                }
                world.spawnParticles(ParticleTypes.FLAME, at.x, at.y + 0.3, at.z, 30, r / 2, 0.3, r / 2, 0.02);
            }
            case "wall" -> {
                Direction facing = owner != null ? owner.getHorizontalFacing() : Direction.NORTH;
                WardSystem.raiseWall(world, BlockPos.ofFloored(at).up(), facing.rotateYClockwise(), 5, 3, ChakraWallBlock.Kind.BARRIER, 160);
            }
            case "seal" -> {
                if (direct instanceof LivingEntity living) {
                    for (ToolEffect fx : proj.effects) fx.apply(living);
                    living.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200));
                }
                world.spawnParticles(ParticleTypes.ENCHANT, at.x, at.y + 0.5, at.z, 30, 0.5, 0.8, 0.5, 0.4);
            }
            default -> world.spawnParticles(ParticleTypes.CRIT, at.x, at.y, at.z, 6, 0.1, 0.1, 0.1, 0.1);
        }
    }

    public static AreaEffectCloudEntity cloud(ServerWorld world, Vec3d at, float radius, List<ToolEffect> effects, int color, @Nullable Entity owner) {
        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(world, at.x, at.y, at.z);
        cloud.setRadius(radius);
        cloud.setDuration(160);
        cloud.setWaitTime(5);
        cloud.setRadiusGrowth(-radius / 200f);
        if (owner instanceof LivingEntity living) cloud.setOwner(living);
        cloud.setPotionContents(new PotionContentsComponent(Optional.empty(), Optional.of(color),
                effects.stream().map(ToolEffect::instance).toList()));
        world.spawnEntity(cloud);
        world.spawnParticles(ParticleTypes.LARGE_SMOKE, at.x, at.y + 0.5, at.z, 25, radius / 2, 0.5, radius / 2, 0.02);
        world.playSound(null, at.x, at.y, at.z, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS, 1f, 0.8f);
        return cloud;
    }

    /** Chain / wire / hook connected with a target entity. */
    public static void chainHit(ThrownToolEntity proj, ServerWorld world, Entity target) {
        Entity owner = proj.getOwner();
        if (proj.damage > 0) target.damage(world.getDamageSources().thrown(proj, owner), proj.damage);
        if (target instanceof LivingEntity living) for (ToolEffect fx : proj.effects) fx.apply(living);
        if (owner != null && !proj.chainMode.equals("bind")) {
            // Applied next tick: the hit's own knockback would otherwise overwrite the pull.
            ToolScheduler.run(2, t -> {
                if (t == 0 || !target.isAlive()) return t == 0;
                Vec3d pull = owner.getPos().subtract(target.getPos());
                double d = pull.length();
                if (d > 1.5) {
                    // ~9 ticks of airtime with 0.91 drag carries ~6x the launch speed: land just in front of the owner.
                    Vec3d v = pull.normalize().multiply(Math.min(2.0, (d - 1.2) * 0.15)).add(0, 0.35, 0);
                    target.setVelocity(v);
                    target.velocityModified = true;
                }
                return false;
            });
        }
        world.playSound(null, target.getBlockPos(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.PLAYERS, 1f, 1f);
    }

    /** Chain hooked into a block: grapple the owner toward it. */
    public static void chainBlock(ThrownToolEntity proj, ServerWorld world, Vec3d at) {
        Entity owner = proj.getOwner();
        if (owner == null || proj.chainMode.equals("bind")) return;
        Vec3d pull = at.subtract(owner.getPos());
        double d = pull.length();
        if (d < 2) return;
        owner.setVelocity(pull.normalize().multiply(Math.min(2.4, (d - 1) * 0.15)).add(0, 0.3, 0));
        owner.velocityModified = true;
        owner.fallDistance = 0f;
        world.playSound(null, owner.getBlockPos(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.PLAYERS, 1f, 1.2f);
    }

    @SuppressWarnings("unused")
    private static void particles(ServerWorld world, Vec3d at, int color) {
        world.spawnParticles(AnimeParticles.dust(color, 1f), at.x, at.y, at.z, 10, 0.3, 0.3, 0.3, 0);
    }
}
