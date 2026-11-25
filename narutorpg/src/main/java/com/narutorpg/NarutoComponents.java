package com.narutorpg;

import com.animecore.component.AnimeComponentInitializer;

/** CCA entrypoint: attaches {@code narutorpg:power} (a PlayerPowerComponent) to every player. */
public final class NarutoComponents extends AnimeComponentInitializer {
    public NarutoComponents() {
        super(() -> NarutoRPG.CTX);
    }
}
