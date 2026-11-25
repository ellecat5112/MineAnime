package com.animecore.datagen;

import com.animecore.ability.AbilityDamage;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

/** {@code ./gradlew :animecore:runDatagen} -> src/main/generated */
public final class AnimeCoreDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(DamageTypes::new);
        pack.addProvider(English::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder builder) {
        builder.addRegistry(RegistryKeys.DAMAGE_TYPE, ctx -> ctx.register(AbilityDamage.ABILITY,
                new DamageType("animecore.ability", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1f)));
    }

    private static final class DamageTypes extends FabricDynamicRegistryProvider {
        DamageTypes(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
            super(output, registries);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
            entries.add(registries.getWrapperOrThrow(RegistryKeys.DAMAGE_TYPE), AbilityDamage.ABILITY);
        }

        @Override public String getName() { return "animecore damage types"; }
    }

    private static final class English extends FabricLanguageProvider {
        English(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
            super(output, "en_us", registries);
        }

        @Override
        public void generateTranslations(RegistryWrapper.WrapperLookup registries, TranslationBuilder t) {
            t.add("entity.animecore.energy_projectile", "Energy Projectile");
            t.add("death.attack.animecore.ability", "%1$s was overwhelmed by a technique");
            t.add("death.attack.animecore.ability.player", "%1$s fell to %2$s's technique");
            t.add("death.attack.animecore.ability.item", "%1$s fell to %2$s's technique");
            t.add("message.animecore.cast.success", "Technique released");
            t.add("message.animecore.cast.charging", "Charging...");
            t.add("message.animecore.cast.armed", "Ready to counter");
            t.add("message.animecore.cast.unknown_ability", "Unknown technique");
            t.add("message.animecore.cast.unknown_system", "That technique belongs to another power system");
            t.add("message.animecore.cast.locked", "You have not learned that yet");
            t.add("message.animecore.cast.tier_too_low", "Your rank is too low");
            t.add("message.animecore.cast.wrong_form", "Requires a transformation");
            t.add("message.animecore.cast.on_cooldown", "Still recovering");
            t.add("message.animecore.cast.no_energy", "Not enough energy");
            t.add("message.animecore.cast.cancelled", "Cancelled");
            t.add("message.animecore.cast.no_match", "That sequence does nothing");
            t.add("message.animecore.reactive_armed", "%s ready");
            t.add("message.animecore.transformed", "%s activated");
            t.add("message.animecore.transformation_ended", "%s faded");
            t.add("message.animecore.evolved", "Your power evolves: %s unlocked!");
            t.add("message.animecore.no_transformation", "No transformation available");
            t.add("commands.animecore.unknown", "Unknown id: %s");
            t.add("commands.animecore.lineage", "%s now belongs to %s");
            t.add("commands.animecore.faction", "%s joined %s");
            t.add("commands.animecore.unlock", "%s learned %s");
        }
    }
}
