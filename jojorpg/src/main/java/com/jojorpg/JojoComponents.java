package com.jojorpg;

import com.animecore.component.AnimeComponentInitializer;

/** CCA entrypoint: attaches {@code jojorpg:power} (a PlayerPowerComponent) to every player. */
public final class JojoComponents extends AnimeComponentInitializer {
    public JojoComponents() {
        super(() -> JojoRPG.CTX);
    }
}
