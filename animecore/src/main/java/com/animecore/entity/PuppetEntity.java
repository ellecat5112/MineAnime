package com.animecore.entity;

import com.animecore.api.AnimeModRegistry;
import com.animecore.api.EnergyPoolSpec;
import com.animecore.platform.AnimeParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

/**
 * Chakra-threaded puppet / drone / toad ally. Fights its owner's enemies, follows the owner, drains a little energy
 * per tick from the owner (chakra threads) and crumbles when its lifetime or the owner's energy runs out.
 * {@code kind} selects the texture/scale, {@code style} the combat behaviour.
 */
public class PuppetEntity extends PathAwareEntity {
    private static final TrackedData<String> KIND = DataTracker.registerData(PuppetEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> STYLE = DataTracker.registerData(PuppetEntity.class, TrackedDataHandlerRegistry.STRING);

    private @Nullable UUID owner;
    private int lifetime = 600;
    private float upkeep = 0.05f;
    private float strike = 4f;
    private @Nullable String system;
    private int shotCooldown;

    public PuppetEntity(EntityType<? extends PuppetEntity> type, World world) {
        super(type, world);
        this.experiencePoints = 0;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.4);
    }

    public void configure(PlayerEntity owner, String system, String kind, String style, float health, float damage,
                          int lifetime, float upkeep, float speed) {
        this.owner = owner.getUuid();
        this.system = system;
        this.lifetime = lifetime;
        this.upkeep = upkeep;
        this.strike = damage;
        dataTracker.set(KIND, kind);
        dataTracker.set(STYLE, style);
        getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(health);
        getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
        getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
        setHealth(health);
        if (style.equals("flying")) setNoGravity(true);
        if (style.equals("guard")) addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, lifetime, 1, false, false));
        setCustomName(owner.getName().copy().append("'s ").append(net.minecraft.text.Text.translatable("entity.animecore.puppet." + kind)));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(KIND, "wood");
        builder.add(STYLE, "melee");
    }

    public String kind() { return dataTracker.get(KIND); }
    public String style() { return dataTracker.get(STYLE); }
    public @Nullable UUID ownerUuid() { return owner; }

    public @Nullable PlayerEntity owner() {
        return owner == null ? null : getWorld().getPlayerByUuid(owner);
    }

    public boolean isAlly(LivingEntity e) {
        if (owner == null) return false;
        if (e.getUuid().equals(owner)) return true;
        return e instanceof PuppetEntity p && owner.equals(p.owner);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(2, new MeleeAttackGoal(this, 1.2, true) {
            @Override public boolean canStart() { return !isRanged() && super.canStart(); }
        });
        goalSelector.add(3, new FollowOwnerGoal());
        goalSelector.add(6, new WanderAroundFarGoal(this, 0.8));
        goalSelector.add(7, new LookAroundGoal(this));
        targetSelector.add(1, new DefendOwnerGoal());
        targetSelector.add(3, new ActiveTargetGoal<>(this, HostileEntity.class, 10, true, false, e -> !isAlly(e)));
    }

    private boolean isRanged() {
        String s = style();
        return s.equals("flame") || s.equals("water") || s.equals("laser") || s.equals("poison");
    }

    @Override
    public void tick() {
        super.tick();
        if (!(getWorld() instanceof ServerWorld world)) return;
        PlayerEntity master = owner();
        if (master == null || --lifetime <= 0 || !payUpkeep(master)) {
            world.spawnParticles(ParticleTypes.POOF, getX(), getBodyY(0.5), getZ(), 12, 0.3, 0.5, 0.3, 0.02);
            discard();
            return;
        }
        LivingEntity target = getTarget();
        if (target != null && isAlly(target)) setTarget(null);
        if (style().equals("flying") && target != null) {
            Vec3d to = target.getEyePos().subtract(getPos()).normalize().multiply(0.35);
            setVelocity(getVelocity().multiply(0.7).add(to));
            if (squaredDistanceTo(target) < 2.5 && age % 15 == 0) tryAttack(target);
        }
        if (isRanged() && target != null && --shotCooldown <= 0 && canSee(target) && squaredDistanceTo(target) < 256) {
            shoot(world, target);
            shotCooldown = 30;
        }
        if (style().equals("explode") && target != null && squaredDistanceTo(target) < 3) {
            world.createExplosion(this, getX(), getY(), getZ(), 2f, World.ExplosionSourceType.NONE);
            discard();
        }
        if (age % 20 == 0) world.spawnParticles(ParticleTypes.END_ROD, getX(), getY() + getHeight() + 0.2, getZ(), 1, 0, 0, 0, 0);
    }

    private boolean payUpkeep(PlayerEntity master) {
        if (upkeep <= 0 || master.isCreative() || system == null) return true;
        return AnimeModRegistry.get(system).map(ctx -> ctx.pool(EnergyPoolSpec.Role.PRIMARY)
                .map(spec -> ctx.power(master).tryConsume(spec.id(), upkeep)).orElse(true)).orElse(true);
    }

    private void shoot(ServerWorld world, LivingEntity target) {
        Vec3d dir = target.getEyePos().subtract(getEyePos()).normalize();
        switch (style()) {
            case "flame" -> {
                SmallFireballEntity fire = new SmallFireballEntity(world, this, dir.multiply(0.4));
                fire.setPosition(getX(), getEyeY(), getZ());
                world.spawnEntity(fire);
            }
            case "water", "laser", "poison" -> {
                int color = style().equals("water") ? 0x4FC3F7 : style().equals("laser") ? 0xFF5252 : 0x9C27B0;
                for (double d = 0.5; d < Math.sqrt(squaredDistanceTo(target)); d += 0.4) {
                    Vec3d p = getEyePos().add(dir.multiply(d));
                    world.spawnParticles(AnimeParticles.dust(color, 0.9f), p.x, p.y, p.z, 1, 0, 0, 0, 0);
                }
                target.damage(getDamageSources().mobAttack(this), strike * 0.8f);
                if (style().equals("poison")) target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 80, 0));
                if (style().equals("water")) target.takeKnockback(0.6, -dir.x, -dir.z);
            }
            default -> {}
        }
    }

    @Override
    public boolean tryAttack(net.minecraft.entity.Entity target) {
        boolean ok = super.tryAttack(target);
        if (ok && target instanceof LivingEntity living) {
            switch (style()) {
                case "poison" -> living.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 0));
                case "trap" -> living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 4));
                case "drain" -> heal(2f);
                default -> {}
            }
        }
        return ok;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getAttacker() instanceof LivingEntity a && isAlly(a)) return false;
        return super.damage(source, amount);
    }

    @Override public boolean canImmediatelyDespawn(double distanceSquared) { return false; }
    @Override protected boolean isDisallowedInPeaceful() { return false; }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (owner != null) nbt.putUuid("owner", owner);
        nbt.putString("kind", kind());
        nbt.putString("style", style());
        nbt.putInt("lifetime", lifetime);
        if (system != null) nbt.putString("system", system);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        owner = nbt.containsUuid("owner") ? nbt.getUuid("owner") : null;
        dataTracker.set(KIND, nbt.getString("kind"));
        dataTracker.set(STYLE, nbt.getString("style"));
        lifetime = nbt.getInt("lifetime");
        system = nbt.contains("system") ? nbt.getString("system") : null;
    }

    // ---------------------------------------------------------------- goals

    private final class FollowOwnerGoal extends Goal {
        FollowOwnerGoal() { setControls(EnumSet.of(Control.MOVE)); }

        @Override
        public boolean canStart() {
            PlayerEntity o = owner();
            return o != null && getTarget() == null && squaredDistanceTo(o) > 36;
        }

        @Override
        public void tick() {
            PlayerEntity o = owner();
            if (o == null) return;
            if (squaredDistanceTo(o) > 400) refreshPositionAndAngles(o.getX(), o.getY(), o.getZ(), getYaw(), getPitch());
            else getNavigation().startMovingTo(o, 1.2);
        }
    }

    private final class DefendOwnerGoal extends Goal {
        DefendOwnerGoal() { setControls(EnumSet.of(Control.TARGET)); }

        @Override
        public boolean canStart() {
            PlayerEntity o = owner();
            if (o == null) return false;
            LivingEntity threat = o.getAttacker() != null ? o.getAttacker() : o.getAttacking();
            return threat != null && threat.isAlive() && !isAlly(threat);
        }

        @Override
        public void start() {
            PlayerEntity o = owner();
            if (o == null) return;
            setTarget(o.getAttacker() != null ? o.getAttacker() : o.getAttacking());
        }
    }
}
