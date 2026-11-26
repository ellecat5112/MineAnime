package com.animecore.entity;

import com.animecore.ability.AbilityDamage;
import com.animecore.platform.AnimeParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Texture-free energy projectile: rendered purely with tinted dust + an optional trail particle, so it costs zero
 * bytes of art in the jar. GeckoLib-modelled projectiles (Rasenshuriken, Spirit Bomb) subclass this in Phase 2.
 */
public class EnergyProjectileEntity extends ThrownEntity {
    private static final TrackedData<Integer> COLOR = DataTracker.registerData(EnergyProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SIZE = DataTracker.registerData(EnergyProjectileEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<String> TRAIL = DataTracker.registerData(EnergyProjectileEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> STYLE = DataTracker.registerData(EnergyProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private float damage;
    private @Nullable Identifier affinity;
    private float aoeRadius;
    private int fireTicks;
    private int lifetime = 60;
    private @Nullable ParticleEffect cachedTrail;
    private String cachedTrailId = "";

    public EnergyProjectileEntity(EntityType<? extends EnergyProjectileEntity> type, World world) {
        super(type, world);
    }

    public EnergyProjectileEntity(World world, LivingEntity owner) {
        super(AnimeEntities.ENERGY_PROJECTILE, owner, world);
    }

    public void configure(float damage, @Nullable Identifier affinity, float aoeRadius, int fireTicks, int lifetime,
                          int color, float size, @Nullable Identifier trail) {
        this.damage = damage;
        this.affinity = affinity;
        this.aoeRadius = aoeRadius;
        this.fireTicks = fireTicks;
        this.lifetime = lifetime;
        this.dataTracker.set(COLOR, color);
        this.dataTracker.set(SIZE, size);
        this.dataTracker.set(TRAIL, trail == null ? "" : trail.toString());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(COLOR, 0xFFFFFF);
        builder.add(SIZE, 0.5f);
        builder.add(TRAIL, "");
        builder.add(STYLE, com.animecore.vfx.VfxStyle.ENERGY.ordinal());
    }

    /** Elemental look; the client renders the swirling ball and trail from it ({@code EnergyProjectileRenderer}). */
    public void setVfx(com.animecore.vfx.VfxStyle style, int color) {
        dataTracker.set(STYLE, style.ordinal());
        dataTracker.set(COLOR, color);
    }

    public com.animecore.vfx.VfxStyle getStyle() { return com.animecore.vfx.VfxStyle.byOrdinal(dataTracker.get(STYLE)); }

    public int getColor() { return dataTracker.get(COLOR); }
    public float getSize() { return dataTracker.get(SIZE); }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (SIZE.equals(data)) calculateDimensions();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        float s = getSize();
        return EntityDimensions.changing(s, s);
    }

    @Override
    protected double getGravity() {
        return 0.0;
    }

    @Override
    public void tick() {
        super.tick();
        World world = getWorld();
        if (world.isClient) {
            spawnTrail(world);
        } else if (age > lifetime) {
            if (aoeRadius > 0f) detonate((ServerWorld) world, null);
            discard();
        }
    }

    private void spawnTrail(World world) {
        // The swirling ball and elemental trail are drawn by the client VFX renderer; only the optional extra trail here.
        float s = getSize();
        String trailId = dataTracker.get(TRAIL);
        if (!trailId.equals(cachedTrailId)) {
            cachedTrailId = trailId;
            cachedTrail = trailId.isEmpty() ? null : AnimeParticles.simple(Identifier.tryParse(trailId));
        }
        if (cachedTrail != null) {
            world.addParticle(cachedTrail, getX(), getY() + s / 2, getZ(), -getVelocity().x * 0.1, -getVelocity().y * 0.1, -getVelocity().z * 0.1);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult hit) {
        super.onEntityHit(hit);
        if (!(getWorld() instanceof ServerWorld world)) return;
        impact(world, Math.max(0.8f, getSize() * 1.5f));
        // the entity actually struck always takes the hit; an explosion then catches everything else around it
        if (hit.getEntity() instanceof LivingEntity target) {
            LivingEntity owner = getOwner() instanceof LivingEntity le ? le : null;
            AbilityDamage.apply(world, this, owner, target, damage, affinity, fireTicks, aoeRadius > 0f ? 0.6f : 0.3f, getPos());
            if (aoeRadius > 0f) detonate(world, target);
        } else if (aoeRadius > 0f) {
            detonate(world, null);
        }
        discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult hit) {
        super.onBlockHit(hit);
        if (getWorld() instanceof ServerWorld world) {
            impact(world, Math.max(0.8f, getSize() * 1.5f));
            if (aoeRadius > 0f) detonate(world, null);
            discard();
        }
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity) && !(entity instanceof EnergyProjectileEntity);
    }

    /** Blast damage to every living entity whose body (not just its feet) is within the radius; {@code skip} was already hit. */
    private void detonate(ServerWorld world, @Nullable Entity skip) {
        LivingEntity owner = getOwner() instanceof LivingEntity le ? le : null;
        Box box = getBoundingBox().expand(aoeRadius);
        Vec3d c = getBoundingBox().getCenter();
        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, box,
                e -> e != owner && e != skip && e.isAlive() && distanceToBox(e.getBoundingBox(), c) <= aoeRadius * aoeRadius)) {
            AbilityDamage.apply(world, this, owner, target, damage, affinity, fireTicks, 0.6f, getPos());
        }
        world.spawnParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
        impact(world, aoeRadius);
        world.playSound(null, getX(), getY(), getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.6f, 1.2f);
        aoeRadius = 0f; // never detonate twice
    }

    private static double distanceToBox(Box b, Vec3d p) {
        double dx = Math.max(0, Math.max(b.minX - p.x, p.x - b.maxX));
        double dy = Math.max(0, Math.max(b.minY - p.y, p.y - b.maxY));
        double dz = Math.max(0, Math.max(b.minZ - p.z, p.z - b.maxZ));
        return dx * dx + dy * dy + dz * dz;
    }

    private void impact(ServerWorld world, float radius) {
        com.animecore.vfx.AbilityVfx.emit(world, this, com.animecore.vfx.VfxKind.IMPACT, getStyle(), getColor(),
                getPos().add(0, getSize() / 2, 0), getVelocity().normalize(), radius, 0f);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat("damage", damage);
        nbt.putFloat("aoe_radius", aoeRadius);
        nbt.putInt("fire_ticks", fireTicks);
        nbt.putInt("lifetime", lifetime);
        nbt.putInt("color", getColor());
        nbt.putFloat("size", getSize());
        nbt.putString("trail", dataTracker.get(TRAIL));
        nbt.putInt("vfx_style", dataTracker.get(STYLE));
        if (affinity != null) nbt.putString("affinity", affinity.toString());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        damage = nbt.getFloat("damage");
        aoeRadius = nbt.getFloat("aoe_radius");
        fireTicks = nbt.getInt("fire_ticks");
        lifetime = nbt.contains("lifetime") ? nbt.getInt("lifetime") : 60;
        dataTracker.set(COLOR, nbt.getInt("color"));
        dataTracker.set(SIZE, nbt.contains("size") ? nbt.getFloat("size") : 0.5f);
        dataTracker.set(TRAIL, nbt.getString("trail"));
        dataTracker.set(STYLE, nbt.getInt("vfx_style"));
        affinity = nbt.contains("affinity") ? Identifier.tryParse(nbt.getString("affinity")) : null;
    }
}
