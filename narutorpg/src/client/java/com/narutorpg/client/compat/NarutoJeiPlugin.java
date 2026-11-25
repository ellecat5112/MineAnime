package com.narutorpg.client.compat;

import com.animecore.api.AnimeModContext;
import com.animecore.compat.jei.AbstractAnimeJeiPlugin;
import com.narutorpg.NarutoRPG;
import mezz.jei.api.JeiPlugin;

/** Scroll subtypes, Technique Scrolls and Energy Infusion categories: all inherited from animecore. */
@JeiPlugin
public final class NarutoJeiPlugin extends AbstractAnimeJeiPlugin {
    @Override
    protected AnimeModContext context() {
        return NarutoRPG.CTX;
    }
}
