package com.animecore.api;

import com.animecore.component.PlayerPowerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/** Small static facade for content mods and integrations (HUD, JEI, Lootr, other mods). */
public final class AnimeCoreApi {
    private AnimeCoreApi() {}

    public static Optional<AnimeModContext> system(String modId) {
        return AnimeModRegistry.get(modId);
    }

    public static Optional<PlayerPowerComponent> power(PlayerEntity player, String modId) {
        return AnimeModRegistry.get(modId).flatMap(ctx -> ctx.maybePower(player));
    }

    /** Every affinity an entity has across all loaded power systems (used for clash calculations). */
    public static Set<Identifier> affinitiesOf(Entity entity) {
        Set<Identifier> out = new LinkedHashSet<>();
        if (entity instanceof PlayerEntity player) {
            for (AnimeModContext ctx : AnimeModRegistry.all()) {
                ctx.maybePower(player).ifPresent(p -> out.addAll(p.effectiveAffinities()));
            }
        }
        return out;
    }
}
