package com.animecore.entity;

import com.animecore.ability.AbilityContext;
import com.animecore.ability.action.IAbilityAction;
import com.animecore.quest.QuestManager;
import com.animecore.quest.StoryNpcDefinition;
import com.animecore.registry.AnimeRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A story character from the {@link AnimeRegistries#STORY_NPC} registry. Its definition (synced to clients) drives the look,
 * stats and behaviour:
 * <ul>
 *   <li>givers stand still, cannot be hurt and talk to players (quest dialogue / greeting);</li>
 *   <li>allies follow the player whose quest summoned them and fight hostile story characters;</li>
 *   <li>enemies and bosses hunt players, fight in melee and cast their real techniques
 *       ({@link AbilityContext#forMob}); bosses show a boss bar.</li>
 * </ul>
 * NPCs spawned for a quest remember the quest and its player and vanish once that quest is no longer active.
 */
public class StoryNpcEntity extends PathAwareEntity {
    private static final TrackedData<String> NPC = DataTracker.registerData(StoryNpcEntity.class, TrackedDataHandlerRegistry.STRING);

    private @Nullable UUID owner;
    private @Nullable Identifier quest;
    private int castCooldown = 40;
    private @Nullable ServerBossBar bossBar;
    private boolean configured;

    public StoryNpcEntity(EntityType<? extends StoryNpcEntity> type, World world) {
        super(type, world);
        this.experiencePoints = 10;
        setPersistent();
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32)
                .add(EntityAttributes.GENERIC_ARMOR, 2);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(NPC, "");
    }

    // ------------------------------------------------------------------ identity

    public Identifier npcId() {
        Identifier id = Identifier.tryParse(dataTracker.get(NPC));
        return id == null ? Identifier.of("animecore", "unknown") : id;
    }

    public Optional<StoryNpcDefinition> definition() {
        return AnimeRegistries.storyNpc(getRegistryManager(), npcId());
    }

    public StoryNpcDefinition.Role role() {
        return definition().map(StoryNpcDefinition::role).orElse(StoryNpcDefinition.Role.ENEMY);
    }

    public @Nullable UUID owner() { return owner; }
    public @Nullable Identifier quest() { return quest; }

    /** Server: become story character {@code id}, spawned for {@code player}'s {@code quest} (both may be null). */
    public void setup(Identifier id, @Nullable ServerPlayerEntity player, @Nullable Identifier quest) {
        dataTracker.set(NPC, id.toString());
        this.owner = player == null ? null : player.getUuid();
        this.quest = quest;
        configured = false;
        configure();
        setHealth(getMaxHealth());
    }

    private void configure() {
        if (configured || getWorld().isClient) return;
        Optional<StoryNpcDefinition> def = definition();
        if (def.isEmpty()) return;
        configured = true;
        StoryNpcDefinition d = def.get();
        set(EntityAttributes.GENERIC_MAX_HEALTH, d.health());
        set(EntityAttributes.GENERIC_ATTACK_DAMAGE, d.attack());
        set(EntityAttributes.GENERIC_MOVEMENT_SPEED, d.speed());
        set(EntityAttributes.GENERIC_SCALE, d.scale());
        Text name = d.title().getString().isEmpty() ? d.name() : d.name().copy().append(Text.literal(" ").append(d.title()));
        setCustomName(name);
        setCustomNameVisible(true);
        d.heldItem().flatMap(Registries.ITEM::getOrEmpty).ifPresent(item -> equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, new ItemStack(item)));
        if (d.role() == StoryNpcDefinition.Role.BOSS && bossBar == null) {
            bossBar = new ServerBossBar(getDisplayName(), BossBar.Color.RED, BossBar.Style.NOTCHED_10);
        }
    }

    private void set(net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attr, double value) {
        EntityAttributeInstance inst = getAttributeInstance(attr);
        if (inst != null) inst.setBaseValue(value);
    }

    // ------------------------------------------------------------------ AI

    @Override
    protected void initGoals() {
        goalSelector.add(0, new SwimGoal(this));
        goalSelector.add(1, new CastGoal());
        goalSelector.add(2, new MeleeAttackGoal(this, 1.15, true) {
            @Override public boolean canStart() { return role() != StoryNpcDefinition.Role.GIVER && super.canStart(); }
        });
        goalSelector.add(3, new FollowOwnerGoal());
        goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8f));
        goalSelector.add(7, new LookAroundGoal(this));
        targetSelector.add(1, new RevengeGoal(this) {
            @Override public boolean canStart() { return role() != StoryNpcDefinition.Role.GIVER && super.canStart(); }
        });
        targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false,
                p -> role().hostile() && !((PlayerEntity) p).isCreative() && !p.isSpectator()));
        targetSelector.add(3, new ActiveTargetGoal<>(this, StoryNpcEntity.class, 10, true, false,
                e -> e instanceof StoryNpcEntity other && other.role().hostile() != role().hostile()
                        && role() != StoryNpcDefinition.Role.GIVER && other.role() != StoryNpcDefinition.Role.GIVER));
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (target instanceof StoryNpcEntity other && other.role().hostile() == role().hostile()) return false;
        if (!role().hostile() && target instanceof PlayerEntity) return false;
        return super.canTarget(target);
    }

    /** Casts one of the character's techniques at its target every {@code cast_interval} ticks (± a little). */
    private final class CastGoal extends Goal {
        CastGoal() { setControls(EnumSet.noneOf(Control.class)); }

        @Override public boolean canStart() {
            LivingEntity t = getTarget();
            return t != null && t.isAlive() && --castCooldown <= 0 && squaredDistanceTo(t) < 24 * 24 && canSee(t)
                    && definition().map(d -> !d.abilities().isEmpty()).orElse(false);
        }

        @Override public boolean shouldContinue() { return false; }

        @Override public void start() {
            StoryNpcDefinition d = definition().orElseThrow();
            castCooldown = d.castInterval() + random.nextInt(Math.max(1, d.castInterval() / 2));
            List<Identifier> list = d.abilities();
            castAt(getTarget(), list.get(random.nextInt(list.size())), d.power());
        }
    }

    /** Server: face {@code target} and run every action of {@code ability}. Returns false if the ability is unknown. */
    public boolean castAt(@Nullable LivingEntity target, Identifier ability, float power) {
        if (!(getWorld() instanceof ServerWorld) ) return false;
        var entry = AnimeRegistries.ability(getRegistryManager(), ability);
        if (entry.isEmpty()) return false;
        if (target != null) face(target);
        AbilityContext ctx = AbilityContext.forMob(this, entry.get(), power);
        for (IAbilityAction action : entry.get().value().actions()) action.execute(ctx);
        return true;
    }

    private void face(Entity target) {
        Vec3d d = target.getEyePos().subtract(getEyePos());
        float yaw = (float) (MathHelper.atan2(d.z, d.x) * MathHelper.DEGREES_PER_RADIAN) - 90f;
        float pitch = (float) -(MathHelper.atan2(d.y, d.horizontalLength()) * MathHelper.DEGREES_PER_RADIAN);
        setYaw(yaw);
        setHeadYaw(yaw);
        setBodyYaw(yaw);
        setPitch(pitch);
        prevYaw = yaw;
        prevPitch = pitch;
    }

    /** Allies stay with the player who started their quest. */
    private final class FollowOwnerGoal extends Goal {
        FollowOwnerGoal() { setControls(EnumSet.of(Control.MOVE)); }

        private @Nullable PlayerEntity ownerPlayer() {
            return owner == null ? null : getWorld().getPlayerByUuid(owner);
        }

        @Override public boolean canStart() {
            PlayerEntity o = ownerPlayer();
            return role() == StoryNpcDefinition.Role.ALLY && o != null && getTarget() == null && squaredDistanceTo(o) > 36;
        }

        @Override public void tick() {
            PlayerEntity o = ownerPlayer();
            if (o == null) return;
            if (squaredDistanceTo(o) > 28 * 28) {
                refreshPositionAndAngles(o.getX() + random.nextInt(5) - 2, o.getY(), o.getZ() + random.nextInt(5) - 2, getYaw(), getPitch());
                getNavigation().stop();
            } else {
                getNavigation().startMovingTo(o, 1.2);
            }
        }
    }

    // ------------------------------------------------------------------ life cycle

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) return;
        configure();
        if (bossBar != null) bossBar.setPercent(getHealth() / getMaxHealth());
        if (age % 40 == 0 && quest != null && !QuestManager.stillNeeded(this)) {
            ((ServerWorld) getWorld()).spawnParticles(ParticleTypes.POOF, getX(), getBodyY(0.5), getZ(), 12, 0.3, 0.6, 0.3, 0.02);
            discard();
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (role() == StoryNpcDefinition.Role.GIVER) return !source.isSourceCreativePlayer() && !source.isOf(net.minecraft.entity.damage.DamageTypes.OUT_OF_WORLD);
        if (role() == StoryNpcDefinition.Role.ALLY && source.getAttacker() instanceof PlayerEntity) return true;
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPushable() { return role() != StoryNpcDefinition.Role.GIVER && super.isPushable(); }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) { return false; }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (getWorld().isClient || hand != Hand.MAIN_HAND) return ActionResult.success(getWorld().isClient);
        if (player instanceof ServerPlayerEntity sp) QuestManager.onTalk(sp, this);
        return ActionResult.CONSUME;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!getWorld().isClient) QuestManager.onNpcDefeated(this, source);
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        if (bossBar != null) bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        if (bossBar != null) bossBar.removePlayer(player);
    }

    @Override
    public void remove(RemovalReason reason) {
        if (bossBar != null) bossBar.clearPlayers();
        super.remove(reason);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putString("npc", dataTracker.get(NPC));
        if (owner != null) nbt.putUuid("owner", owner);
        if (quest != null) nbt.putString("quest", quest.toString());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        dataTracker.set(NPC, nbt.getString("npc"));
        owner = nbt.containsUuid("owner") ? nbt.getUuid("owner") : null;
        quest = nbt.contains("quest") ? Identifier.tryParse(nbt.getString("quest")) : null;
    }
}
