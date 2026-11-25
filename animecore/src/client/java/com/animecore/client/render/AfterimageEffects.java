package com.animecore.client.render;

import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.network.MovementPayloads;
import com.animecore.platform.AnimeParticles;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

/** Burst-step afterimages: fading player-height dust silhouettes along the blink path (procedural, no textures). */
public final class AfterimageEffects {
    private AfterimageEffects() {}

    public static void init() {
        AnimeClientNetworking.registerReceiver(MovementPayloads.Afterimage.ID, (payload, client) -> {
            if (client.world != null) spawn(client.world, new Vec3d(payload.from()), new Vec3d(payload.to()), payload.color());
        });
    }

    public static void spawn(ClientWorld world, Vec3d from, Vec3d to, int color) {
        Vec3d delta = to.subtract(from);
        double length = delta.length();
        int silhouettes = Math.max(2, (int) (length / 0.9));
        for (int i = 0; i <= silhouettes; i++) {
            double t = i / (double) silhouettes;
            Vec3d p = from.add(delta.multiply(t));
            float fade = (float) (0.35 + 0.65 * t);
            for (double h = 0.1; h < 1.9; h += 0.3) {
                double width = h > 1.4 ? 0.12 : 0.22;
                world.addParticle(AnimeParticles.dust(color, fade), p.x + (world.random.nextDouble() - 0.5) * width,
                        p.y + h, p.z + (world.random.nextDouble() - 0.5) * width, 0, 0, 0);
            }
        }
        world.addParticle(ParticleTypes.CLOUD, from.x, from.y + 1, from.z, 0, 0.02, 0);
    }
}
