package com.animecore.client.render;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.component.EnergyPool;
import com.animecore.component.PlayerPowerComponent;
import com.animecore.platform.AnimeParticles;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.AbilityDefinition;
import com.animecore.registry.definition.AffinityDefinition;
import com.animecore.registry.definition.TransformationDefinition;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

import java.util.Optional;

/**
 * Texture-free auras: Ki flames, Reiatsu pressure and Chakra cloaks are tinted dust driven by the synced public
 * state (active form, aura color/intensity, pool ratio, charging). Zero network cost per tick, zero sprite bytes,
 * and it honours the vanilla particle setting.
 */
public final class ProceduralAuraRenderer {
    private ProceduralAuraRenderer() {}

    public static void init() {
        ClientTickEvents.END_WORLD_TICK.register(ProceduralAuraRenderer::tick);
    }

    private static void tick(ClientWorld world) {
        MinecraftClient client = MinecraftClient.getInstance();
        ParticlesMode mode = client.options.getParticles().getValue();
        if (mode == ParticlesMode.MINIMAL) return;
        float budget = mode == ParticlesMode.DECREASED ? 0.4f : 1f;
        for (AbstractClientPlayerEntity player : world.getPlayers()) {
            if (player.isInvisible() || player.isSpectator()) continue;
            boolean firstPersonSelf = player == client.player && client.options.getPerspective().isFirstPerson();
            for (AnimeModContext ctx : AnimeModRegistry.all()) {
                ctx.maybePower(player).ifPresent(power -> {
                    aura(world, player, power, budget, firstPersonSelf);
                    charge(world, player, power, budget, firstPersonSelf);
                });
            }
        }
    }

    private static void aura(ClientWorld world, AbstractClientPlayerEntity player, PlayerPowerComponent power,
                             float budget, boolean firstPersonSelf) {
        Optional<TransformationDefinition> form = power.activeTransformation()
                .flatMap(id -> AnimeRegistries.transformation(world.getRegistryManager(), id)).map(RegistryEntry::value);
        if (form.isEmpty() || form.get().visuals().auraIntensity() <= 0f) return;
        TransformationDefinition.Visuals visuals = form.get().visuals();
        float energy = form.flatMap(def -> power.pool(def.pool())).map(EnergyPool::ratio).orElse(1f);
        float strength = visuals.auraIntensity() * (0.4f + 0.6f * energy) * budget;
        Random random = player.getRandom();
        int count = MathHelper.floor(strength * 2.5f) + (random.nextFloat() < (strength * 2.5f) % 1f ? 1 : 0);
        float maxHeight = firstPersonSelf ? 0.9f : 2.1f; // keep the camera clear in first person
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.35 + random.nextDouble() * 0.25 * visuals.auraIntensity();
            double h = random.nextDouble() * maxHeight;
            float scale = 0.6f + random.nextFloat() * 0.8f * visuals.auraIntensity();
            int color = random.nextFloat() < 0.25f ? lighten(visuals.auraColor()) : visuals.auraColor();
            world.addParticle(AnimeParticles.dust(color, scale),
                    player.getX() + Math.cos(angle) * radius, player.getY() + h, player.getZ() + Math.sin(angle) * radius,
                    0, 0.6 + random.nextDouble() * 0.6, 0);
        }
    }

    private static void charge(ClientWorld world, AbstractClientPlayerEntity player, PlayerPowerComponent power,
                               float budget, boolean firstPersonSelf) {
        if (power.chargingAbility().isEmpty() || firstPersonSelf && player.age % 2 == 0) return;
        var rm = world.getRegistryManager();
        int color = power.chargingAbility().flatMap(id -> AnimeRegistries.ability(rm, id)).map(RegistryEntry::value)
                .flatMap(AbilityDefinition::affinity).flatMap(a -> AnimeRegistries.affinities(rm).getOrEmpty(a))
                .map(AffinityDefinition::color).orElse(power.context().pools().get(0).color());
        double t = player.age * 0.45;
        double yaw = Math.toRadians(player.bodyYaw);
        double fx = -Math.sin(yaw) * 0.55, fz = Math.cos(yaw) * 0.55;
        int spokes = budget < 1f ? 1 : 3;
        for (int i = 0; i < spokes; i++) {
            double a = t + i * (Math.PI * 2 / spokes);
            world.addParticle(AnimeParticles.dust(color, 0.7f),
                    player.getX() + fx + Math.cos(a) * 0.3, player.getY() + 1.1 + Math.sin(a) * 0.3, player.getZ() + fz,
                    0, 0, 0);
        }
    }

    private static int lighten(int rgb) {
        int r = Math.min(255, ((rgb >> 16) & 0xFF) + 80);
        int g = Math.min(255, ((rgb >> 8) & 0xFF) + 80);
        int b = Math.min(255, (rgb & 0xFF) + 80);
        return (r << 16) | (g << 8) | b;
    }
}
