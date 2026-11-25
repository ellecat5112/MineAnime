package com.narutorpg.datagen;

import com.animecore.registry.AnimeRegistries;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/** {@code ./gradlew :narutorpg:runDatagen} writes {@code src/main/generated/data/narutorpg/animecore/**}. */
public final class NarutoDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(Content::new);
        pack.addProvider(NarutoLangProvider::new);
        pack.addProvider(NarutoLootTableProvider::new);
        pack.addProvider(NarutoModelProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder builder) {
        builder.addRegistry(AnimeRegistries.AFFINITY, NarutoContentBootstrap::affinities);
        builder.addRegistry(AnimeRegistries.LINEAGE, NarutoContentBootstrap::lineages);
        builder.addRegistry(AnimeRegistries.FACTION, NarutoContentBootstrap::factions);
        builder.addRegistry(AnimeRegistries.ABILITY, NarutoContentBootstrap::abilities);
        builder.addRegistry(AnimeRegistries.TRANSFORMATION, NarutoContentBootstrap::transformations);
    }

    private static final class Content extends FabricDynamicRegistryProvider {
        Content(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
            super(output, registries);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.AFFINITY));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.LINEAGE));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.FACTION));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.ABILITY));
            entries.addAll(registries.getWrapperOrThrow(AnimeRegistries.TRANSFORMATION));
        }

        @Override public String getName() { return "NarutoRPG content"; }
    }
}
