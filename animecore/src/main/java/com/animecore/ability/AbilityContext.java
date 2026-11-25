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

/** Immutable per-cast data handed to every {@link com.animecore.ability.action.IAbilityAction}. */
public final class AbilityContext {
    /** A fully charged ability deals {@code 1 + CHARGE_BONUS} times its base damage. */
    public static final float CHARGE_BONUS = 1.5f;

    private final ServerPlayerEntity caster;
    private final RegistryEntry<AbilityDefinition> ability;
    private final PlayerPowerComponent power;
    private final float chargeRatio;
    private final @Nullable Entity reactiveAttacker;

    public AbilityContext(ServerPlayerEntity caster, RegistryEntry<AbilityDefinition> ability, PlayerPowerComponent power,
                          float chargeRatio, @Nullable Entity reactiveAttacker) {
        this.caster = caster;
        this.ability = ability;
        this.power = power;
        this.chargeRatio = Math.max(0f, Math.min(1f, chargeRatio));
        this.reactiveAttacker = reactiveAttacker;
    }

    public ServerPlayerEntity caster() { return caster; }
    public ServerWorld world() { return caster.getServerWorld(); }
    public RegistryEntry<AbilityDefinition> ability() { return ability; }
    public AbilityDefinition definition() { return ability.value(); }
    public PlayerPowerComponent power() { return power; }
    public float chargeRatio() { return chargeRatio; }
    public Optional<Entity> reactiveAttacker() { return Optional.ofNullable(reactiveAttacker); }
    public @Nullable Identifier affinity() { return definition().affinity().orElse(null); }

    /** Charge scaling (CHARGED abilities only). */
    public float scale() {
        return definition().castType() == CastType.CHARGED ? 1f + CHARGE_BONUS * chargeRatio : 1f;
    }

    /** Base damage scaled by charge and the caster's trait multipliers (affinity clash is applied per target). */
    public float scaledDamage(float base) {
        return base * scale() * power.traits().abilityPower(affinity());
    }

    public Vec3d eyePos() { return caster.getEyePos(); }
    public Vec3d look() { return caster.getRotationVec(1f); }

    public boolean damage(LivingEntity target, float baseDamage, int fireTicks, float knockback) {
        return AbilityDamage.apply(world(), caster, caster, target, scaledDamage(baseDamage), affinity(), fireTicks, knockback, caster.getPos());
    }
}
