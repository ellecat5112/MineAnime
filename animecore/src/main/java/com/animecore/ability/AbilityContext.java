package com.animecore.ability;

import com.animecore.component.PlayerPowerComponent;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.CastType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Immutable per-cast data handed to every {@link com.animecore.ability.action.IAbilityAction}. The caster is any living
 * entity: players cast through {@link AbilityExecutor} (with their power component), story NPCs and other mobs cast with
 * a flat power multiplier ({@link #forMob}).
 */
public final class AbilityContext {
    /** A fully charged ability deals {@code 1 + CHARGE_BONUS} times its base damage. */
    public static final float CHARGE_BONUS = 1.5f;

    private final LivingEntity caster;
    private final float mobPower;
    private final RegistryEntry<AbilityDefinition> ability;
    private final @Nullable PlayerPowerComponent power;
    private final float chargeRatio;
    private final @Nullable Entity reactiveAttacker;
    private final com.animecore.vfx.VfxStyle vfxStyle;
    private final int vfxColor;

    public AbilityContext(ServerPlayerEntity caster, RegistryEntry<AbilityDefinition> ability, PlayerPowerComponent power,
                          float chargeRatio, @Nullable Entity reactiveAttacker) {
        this(caster, ability, power, 1f, chargeRatio, reactiveAttacker);
    }

    /** A non-player caster (story NPC, boss): damage is the ability's base damage times {@code power}. */
    public static AbilityContext forMob(LivingEntity caster, RegistryEntry<AbilityDefinition> ability, float power) {
        return new AbilityContext(caster, ability, null, power, 0f, null);
    }

    private AbilityContext(LivingEntity caster, RegistryEntry<AbilityDefinition> ability, @Nullable PlayerPowerComponent power,
                           float mobPower, float chargeRatio, @Nullable Entity reactiveAttacker) {
        this.caster = caster;
        this.mobPower = mobPower;
        this.ability = ability;
        this.power = power;
        this.chargeRatio = Math.max(0f, Math.min(1f, chargeRatio));
        this.reactiveAttacker = reactiveAttacker;
        var rm = caster.getRegistryManager();
        this.vfxStyle = com.animecore.vfx.AbilityVfx.resolveStyle(rm, ability.value(), ability.getKey().map(k -> k.getValue())
                .orElse(com.animecore.platform.Ids.core("unknown")));
        this.vfxColor = com.animecore.vfx.AbilityVfx.resolveColor(rm, ability.value(), vfxStyle);
    }

    /** Resolved elemental look of this cast (see {@link com.animecore.vfx.AbilityVfx#resolveStyle}). */
    public com.animecore.vfx.VfxStyle vfxStyle() { return vfxStyle; }
    public int vfxColor() { return vfxColor; }

    public LivingEntity caster() { return caster; }
    /** The casting player, or null for an NPC caster. */
    public @Nullable ServerPlayerEntity player() { return caster instanceof ServerPlayerEntity p ? p : null; }
    public ServerWorld world() { return (ServerWorld) caster.getWorld(); }
    public RegistryEntry<AbilityDefinition> ability() { return ability; }
    public AbilityDefinition definition() { return ability.value(); }
    /** The caster's power component; null for NPC casters. */
    public @Nullable PlayerPowerComponent power() { return power; }
    public float chargeRatio() { return chargeRatio; }
    public Optional<Entity> reactiveAttacker() { return Optional.ofNullable(reactiveAttacker); }
    public @Nullable Identifier affinity() { return definition().affinity().orElse(null); }

    /** Charge scaling (CHARGED abilities only). */
    public float scale() {
        return definition().castType() == CastType.CHARGED ? 1f + CHARGE_BONUS * chargeRatio : 1f;
    }

    /** Base damage scaled by charge and the caster's trait multipliers (affinity clash is applied per target). */
    public float scaledDamage(float base) {
        return base * scale() * (power != null ? power.traits().abilityPower(affinity()) : mobPower);
    }

    public Vec3d eyePos() { return caster.getEyePos(); }
    public Vec3d look() { return caster.getRotationVec(1f); }

    public boolean damage(LivingEntity target, float baseDamage, int fireTicks, float knockback) {
        return AbilityDamage.apply(world(), caster, caster, target, scaledDamage(baseDamage), affinity(), fireTicks, knockback, caster.getPos());
    }
}
