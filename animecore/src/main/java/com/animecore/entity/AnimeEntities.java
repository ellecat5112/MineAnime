package com.animecore.entity;

import com.animecore.platform.Ids;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class AnimeEntities {
    private AnimeEntities() {}

    /** One shared, data-configured projectile type for every texture-free energy attack of every anime mod. */
    public static final EntityType<EnergyProjectileEntity> ENERGY_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
            Ids.core("energy_projectile"),
            EntityType.Builder.<EnergyProjectileEntity>create(EnergyProjectileEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f)
                    .maxTrackingRange(8)
                    .trackingTickInterval(1)
                    .build("energy_projectile"));

    public static void init() {}
}
