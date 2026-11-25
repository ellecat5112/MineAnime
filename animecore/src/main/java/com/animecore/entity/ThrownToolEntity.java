package com.animecore.entity;

import com.animecore.tool.ToolEffect;
import com.animecore.tool.ToolImpacts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Any thrown ninja tool. Renders as its own item sprite (no extra textures). Behaviour is configured per throw:
 * damage, pierce, boomerang return, drop-on-landing, ground trap, timed fuse, Flying Thunder God mark, chain hook,
 * and an impact kind handled by {@link ToolImpacts} (explode / cloud / flash / ice / spikes / fire / wall / seal ...).
 */
public class ThrownToolEntity extends ThrownItemEntity {
    private static final TrackedData<Boolean> STUCK = DataTracker.registerData(ThrownToolEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> TINT = DataTracker.registerData(ThrownToolEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public float damage = 4f;
    public double gravity = 0.03;
    public int pierce;
    public boolean returns;
    public boolean dropOnLand;
    public boolean trap;
    public boolean mark;
    public String chainMode = "";
    public double chainRange = 16;
    public int fuse;
    public float radius = 2f;
    public String impact = "";
    public List<ToolEffect> effects = new ArrayList<>();
    public int trapLife = 400;
    private boolean returning;
    private final Set<UUID> hit = new HashSet<>();

    public ThrownToolEntity(EntityType<? extends ThrownToolEntity> type, World world) {
        super(type, world);
    }

    public ThrownToolEntity(World world, LivingEntity owner, ItemStack stack) {
        super(AnimeEntities.THROWN_TOOL, owner, world);
        setItem(stack.copyWithCount(1));
    }

    @Override protected Item getDefaultItem() { return Items.IRON_NUGGET; }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(STUCK, false);
        builder.add(TINT, -1);
    }

    public boolean isStuck() { return dataTracker.get(STUCK); }
    public void setTint(int color) { dataTracker.set(TINT, color); }
    public int tint() { return dataTracker.get(TINT); }

    @Override protected double getGravity() { return isStuck() || returning ? 0.0 : gravity; }

    private void stick() {
        dataTracker.set(STUCK, true);
        setVelocity(Vec3d.ZERO);
        setNoGravity(true);
    }

    @Override
    public void tick() {
        if (isStuck()) {
            baseTick();
            tickStuck();
            return;
        }
        super.tick();
        World world = getWorld();
        Entity owner = getOwner();
        if (world.isClient) {
            if (!chainMode.isEmpty() || dataTracker.get(TINT) != -1) trail(world, owner);
            return;
        }
        if (!chainMode.isEmpty() && owner != null && squaredDistanceTo(owner) > chainRange * chainRange) discard();
        if (returns && !returning && age > 18) returning = true;
        if (returning && owner != null) {
            Vec3d to = owner.getEyePos().subtract(getPos());
            if (to.lengthSquared() < 2.0 || age > 200) {
                discard();
                return;
            }
            setVelocity(to.normalize().multiply(1.6));
            noClip = true;
        }
        if (age > 400) discard();
    }

    private void trail(World world, Entity owner) {
        if (owner != null && !chainMode.isEmpty()) {
            Vec3d from = owner.getPos().add(0, owner.getHeight() * 0.6, 0);
            Vec3d to = getPos();
            for (int k = 0; k < 6; k++) {
                Vec3d p = from.lerp(to, world.random.nextDouble());
                world.addParticle(ParticleTypes.CRIT, p.x, p.y, p.z, 0, 0, 0);
            }
        }
    }

    private void tickStuck() {
        if (!(getWorld() instanceof ServerWorld world)) return;
        if (fuse > 0) {
            if (age % 4 == 0) world.spawnParticles(ParticleTypes.SMOKE, getX(), getY() + 0.2, getZ(), 1, 0, 0, 0, 0.01);
            if (--fuse == 0) {
                ToolImpacts.apply(this, world, getPos(), null);
                discard();
            }
            return;
        }
        if (mark) {
            if (age % 10 == 0) world.spawnParticles(ParticleTypes.ENCHANT, getX(), getY() + 0.3, getZ(), 2, 0.1, 0.1, 0.1, 0.1);
            return;
        }
        if (trap) {
            if (--trapLife <= 0) {
                discard();
                return;
            }
            for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, getBoundingBox().expand(0.6, 0.4, 0.6),
                    e -> e.isAlive() && e != getOwner() && !(e instanceof PlayerEntity p && p.isSpectator()))) {
                if (e.age % 10 != 0 && hit.contains(e.getUuid())) continue;
                hit.add(e.getUuid());
                e.damage(getDamageSources().thrown(this, getOwner()), damage);
                for (ToolEffect fx : effects) fx.apply(e);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult result) {
        if (!(getWorld() instanceof ServerWorld world) || returning) return;
        Entity target = result.getEntity();
        if (target == getOwner() || !hit.add(target.getUuid())) return;
        if (!chainMode.isEmpty()) {
            ToolImpacts.chainHit(this, world, target);
            discard();
            return;
        }
        if (damage > 0) target.damage(getDamageSources().thrown(this, getOwner()), damage);
        if (target instanceof LivingEntity living) for (ToolEffect fx : effects) fx.apply(living);
        if (!impact.isEmpty() && !impact.equals("dart") && !impact.equals("arrow") && !impact.equals("pebble")) {
            ToolImpacts.apply(this, world, getPos(), target);
            discard();
            return;
        }
        if (pierce-- > 0) return;
        if (returns) {
            returning = true;
            return;
        }
        landItem(world);
        discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult result) {
        super.onBlockHit(result);
        if (!(getWorld() instanceof ServerWorld world) || returning) return;
        if (!chainMode.isEmpty()) {
            ToolImpacts.chainBlock(this, world, result.getPos());
            discard();
            return;
        }
        if (mark || trap || fuse > 0) {
            setPosition(result.getPos());
            stick();
            return;
        }
        if (!impact.isEmpty() && !impact.equals("dart") && !impact.equals("arrow") && !impact.equals("pebble")) {
            ToolImpacts.apply(this, world, result.getPos(), null);
            discard();
            return;
        }
        if (returns) {
            returning = true;
            return;
        }
        landItem(world);
        discard();
    }

    private void landItem(ServerWorld world) {
        if (!dropOnLand) return;
        ItemEntity drop = new ItemEntity(world, getX(), getY(), getZ(), getStack().copy());
        drop.setPickupDelay(10);
        world.spawnEntity(drop);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat("damage", damage);
        nbt.putBoolean("mark", mark);
        nbt.putBoolean("trap", trap);
        nbt.putBoolean("stuck", isStuck());
        nbt.putInt("trap_life", trapLife);
        nbt.putString("impact", impact);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        damage = nbt.getFloat("damage");
        mark = nbt.getBoolean("mark");
        trap = nbt.getBoolean("trap");
        trapLife = nbt.getInt("trap_life");
        impact = nbt.getString("impact");
        if (nbt.getBoolean("stuck")) stick();
    }
}
