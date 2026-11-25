package com.narutorpg.test;

import com.animecore.api.AnimeModContext;
import com.animecore.component.AnimeComponentInitializer;
import net.fabricmc.api.ModInitializer;

/** A second, Dragon-Ball-like power system living next to NarutoRPG: proves multi-mod coexistence + flight. */
public final class FlightTestSystem implements ModInitializer {
    public static final AnimeModContext CTX = AnimeModContext.builder("animetest")
            .primaryEnergy("ki", 200f, 0.5f, 0xFFD54F)
            .flight("ki", 5f, 0.1f, 0.08f, 1)
            .register();

    @Override public void onInitialize() {}

    public static final class Components extends AnimeComponentInitializer {
        public Components() { super(() -> CTX); }
    }
}
