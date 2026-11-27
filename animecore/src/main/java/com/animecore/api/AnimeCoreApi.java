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

    /**
     * Undead for every anime mod: vanilla undead mobs, story characters flagged {@code undead} (vampires, zombies, Pillar
     * Men) and players whose lineage or active form carries the {@code animecore:undead} trait.
     */
    public static boolean isUndead(Entity entity) {
        if (entity instanceof net.minecraft.entity.LivingEntity le && le.getType().isIn(net.minecraft.registry.tag.EntityTypeTags.UNDEAD)) return true;
        if (entity instanceof com.animecore.entity.StoryNpcEntity npc) return npc.definition().map(com.animecore.quest.StoryNpcDefinition::undead).orElse(false);
        if (entity instanceof net.minecraft.server.network.ServerPlayerEntity player) {
            for (AnimeModContext ctx : AnimeModRegistry.all()) {
                for (com.animecore.clan.IClanTrait t : com.animecore.clan.ClanTraitHooks.traits(player, ctx.power(player))) {
                    if (t instanceof com.animecore.clan.ClanTraits.Undead) return true;
                }
            }
        }
        return false;
    }
}
