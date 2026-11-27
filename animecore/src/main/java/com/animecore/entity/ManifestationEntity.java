package com.animecore.entity;

import com.animecore.manifestation.ManifestationDefinition;
import com.animecore.manifestation.ManifestationService;
import com.animecore.registry.AnimeRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A manifested Stand (or any {@link ManifestationDefinition}): an intangible, invulnerable spirit that hovers behind its
 * user's shoulder, steps in front of them to punch and rush, and flies out to strike remote targets. It never saves to
 * disk - {@link ManifestationService} re-summons it while its user keeps it out.
 * <p>
 * Synced state: the definition id, the owner's entity id and the current {@link State} (drives the client animation).
 */
public class ManifestationEntity extends MobEntity {
    private static final TrackedData<String> DEF = DataTracker.registerData(ManifestationEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> OWNER = DataTracker.registerData(ManifestationEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> STATE = DataTracker.registerData(ManifestationEntity.class, TrackedDataHandlerRegistry.BYTE);

    public enum State { IDLE, PUNCH, RUSH, STRIKE, CAST }

    private int stateTicks;
    private @Nullable Vec3d strikePos;
    /** client: when the current state started (for animation phase) */
    public int clientStateStart;
    private State lastClientState = State.IDLE;

    public ManifestationEntity(EntityType<? extends ManifestationEntity> type, World world) {
        super(type, world);
        setNoGravity(true);
        noClip = true;
        setInvulnerable(true);
        setSilent(true);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 20).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DEF, "");
        builder.add(OWNER, -1);
        builder.add(STATE, (byte) 0);
    }

    // ------------------------------------------------------------------ identity

    public void setup(Identifier definition, LivingEntity owner) {
        dataTracker.set(DEF, definition.toString());
        dataTracker.set(OWNER, owner.getId());
        definition().ifPresent(d -> {
            var scale = getAttributeInstance(EntityAttributes.GENERIC_SCALE);
            if (scale != null) scale.setBaseValue(d.scale());
        });
        Vec3d p = restingPos(owner);
        refreshPositionAndAngles(p.x, p.y, p.z, owner.getYaw(), 0f);
    }

    public Identifier definitionId() {
        Identifier id = Identifier.tryParse(dataTracker.get(DEF));
        return id == null ? Identifier.of("animecore", "unknown") : id;
    }

    public Optional<ManifestationDefinition> definition() {
        return AnimeRegistries.manifestation(getRegistryManager(), definitionId());
    }

    public @Nullable LivingEntity owner() {
        Entity e = getWorld().getEntityById(dataTracker.get(OWNER));
        return e instanceof LivingEntity l ? l : null;
    }

    public State state() {
        byte b = dataTracker.get(STATE);
        return b >= 0 && b < State.values().length ? State.values()[b] : State.IDLE;
    }

    /** Server: play {@code state} for {@code ticks} (then back to idle). */
    public void act(State state, int ticks) {
        dataTracker.set(STATE, (byte) state.ordinal());
        stateTicks = ticks;
    }

    /** Server: fly to {@code pos} (remote strike); {@link #act} keeps the state timer. */
    public void strikeAt(Vec3d pos, int ticks) {
        strikePos = pos;
        act(State.STRIKE, ticks);
    }

    // ------------------------------------------------------------------ movement

    private Vec3d restingPos(LivingEntity owner) {
        ManifestationDefinition.Kind kind = definition().map(ManifestationDefinition::kind).orElse(ManifestationDefinition.Kind.CLOSE_RANGE);
        float yaw = owner.bodyYaw * MathHelper.RADIANS_PER_DEGREE;
        Vec3d forward = new Vec3d(-MathHelper.sin(yaw), 0, MathHelper.cos(yaw));
        Vec3d right = new Vec3d(-forward.z, 0, forward.x);
        double bob = Math.sin((age + getId()) * 0.08) * 0.08;
        if (kind == ManifestationDefinition.Kind.BOUND) {
            return owner.getPos().add(right.multiply(-0.55)).add(forward.multiply(0.1)).add(0, 0.15 + bob, 0);
        }
        // behind and above the user's shoulder, the classic Stand pose
        return owner.getPos().add(forward.multiply(-0.75)).add(right.multiply(-0.45)).add(0, 0.35 + bob, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) {
            if (state() != lastClientState) {
                lastClientState = state();
                clientStateStart = age;
            }
            return;
        }
        LivingEntity owner = owner();
        if (owner == null || !owner.isAlive() || owner.isRemoved() || owner.getWorld() != getWorld()
                || !ManifestationService.isManifesting(owner, this)) {
            discard();
            return;
        }
        if (stateTicks > 0 && --stateTicks == 0) {
            dataTracker.set(STATE, (byte) State.IDLE.ordinal());
            strikePos = null;
        }
        Vec3d target;
        float yaw = owner.getYaw();
        switch (state()) {
            case PUNCH, RUSH, CAST -> {
                Vec3d look = Vec3d.fromPolar(0, owner.getYaw());
                target = owner.getPos().add(look.multiply(state() == State.CAST ? 0.9 : 1.25)).add(0, 0.1, 0);
            }
            case STRIKE -> target = strikePos != null ? strikePos.subtract(0, getHeight() * 0.5, 0) : restingPos(owner);
            default -> target = restingPos(owner);
        }
        Vec3d pos = getPos();
        double speed = state() == State.STRIKE ? 0.6 : 0.45;
        Vec3d next = pos.lerp(target, speed);
        if (pos.squaredDistanceTo(target) > 30 * 30) next = target;   // teleported / respawned owner
        if (state() == State.STRIKE && strikePos != null) {
            Vec3d d = strikePos.subtract(pos);
            yaw = (float) (MathHelper.atan2(d.z, d.x) * MathHelper.DEGREES_PER_RADIAN) - 90f;
        }
        setPosition(next);
        setVelocity(Vec3d.ZERO);
        setYaw(yaw);
        setBodyYaw(yaw);
        setHeadYaw(owner.getHeadYaw());
        setPitch(owner.getPitch());
    }

    // ------------------------------------------------------------------ intangible

    @Override public boolean isInvulnerableTo(DamageSource source) { return !source.isOf(net.minecraft.entity.damage.DamageTypes.OUT_OF_WORLD); }
    @Override public boolean canHit() { return false; }          // players hit straight through their Stand
    @Override public boolean isPushable() { return false; }
    @Override public boolean isCollidable() { return false; }
    @Override protected void pushAway(Entity entity) { }
    @Override public boolean isAttackable() { return false; }
    @Override public boolean shouldSave() { return false; }
    @Override public boolean cannotDespawn() { return true; }
    @Override public boolean canBeLeashed() { return false; }
    @Override public Arm getMainArm() { return Arm.RIGHT; }
    @Override public boolean isFireImmune() { return true; }
    @Override public boolean hasNoDrag() { return true; }
    @Override public void writeCustomDataToNbt(NbtCompound nbt) { }
    @Override public void readCustomDataFromNbt(NbtCompound nbt) { }
}
