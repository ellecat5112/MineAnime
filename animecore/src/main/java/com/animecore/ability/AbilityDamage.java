package com.animecore.ability;

import com.animecore.affinity.AffinityMatrix;
import com.animecore.api.AnimeCoreApi;
import com.animecore.platform.Ids;
import com.animecore.registry.AnimeRegistries;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

/** Single damage pipeline shared by every action and energy projectile. */
public final class AbilityDamage {
    private AbilityDamage() {}

    /** {@code data/animecore/damage_type/ability.json} (generated). */
    public static final RegistryKey<DamageType> ABILITY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Ids.core("ability"));

    public static DamageSource source(ServerWorld world, @Nullable Entity direct, @Nullable Entity attacker) {
        return world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getEntry(ABILITY)
                .map(type -> new DamageSource(type, direct, attacker))
                .orElseGet(() -> world.getDamageSources().indirectMagic(direct, attacker));
    }

    /**
     * @param damage already scaled by charge and caster traits; the affinity clash vs the target is applied here
     * @return true if the target took damage
     */
    public static boolean apply(ServerWorld world, @Nullable Entity direct, @Nullable LivingEntity attacker, LivingEntity target,
                                float damage, @Nullable Identifier affinity, int fireTicks, float knockback, Vec3d from) {
        if (target == attacker || !target.isAlive()) return false;
        float clash = affinity == null ? 1f
                : AffinityMatrix.clash(AnimeRegistries.affinities(world.getRegistryManager()), affinity, AnimeCoreApi.affinitiesOf(target));
        boolean hit = damage <= 0f || target.damage(source(world, direct, attacker), damage * clash);
        if (hit) {
            if (fireTicks > 0) target.setOnFireForTicks(fireTicks);
            if (knockback > 0f) target.takeKnockback(knockback, from.x - target.getX(), from.z - target.getZ());
        }
        return hit;
    }
}
