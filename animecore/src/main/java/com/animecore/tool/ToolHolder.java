package com.animecore.tool;

import com.animecore.api.AnimeModContext;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

/** Implemented by every item driven by a {@link ToolSpec} (plain tools and wearable tools). */
public interface ToolHolder {
    ToolSpec spec();

    /** Power system that pays for the moves (chakra, ki...). */
    AnimeModContext context();

    /** The held move currently being used by {@code player} (guard / charge / consume), if any. */
    default Optional<MoveSpec> activeHeld(PlayerEntity player) {
        return ToolBehaviour.activeHeld(this, player);
    }
}
