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

    public static final EntityType<ThrownToolEntity> THROWN_TOOL = Registry.register(Registries.ENTITY_TYPE,
            Ids.core("thrown_tool"),
            EntityType.Builder.<ThrownToolEntity>create(ThrownToolEntity::new, SpawnGroup.MISC)
                    .dimensions(0.35f, 0.35f).maxTrackingRange(6).trackingTickInterval(2).build("thrown_tool"));

    public static final EntityType<PuppetEntity> PUPPET = Registry.register(Registries.ENTITY_TYPE,
            Ids.core("puppet"),
            EntityType.Builder.<PuppetEntity>create(PuppetEntity::new, SpawnGroup.MISC)
                    .dimensions(0.6f, 1.9f).maxTrackingRange(8).build("puppet"));

    public static void init() {
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(PUPPET, PuppetEntity.createAttributes());
    }
}
