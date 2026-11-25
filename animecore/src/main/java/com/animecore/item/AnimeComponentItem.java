package com.animecore.item;

import net.minecraft.component.ComponentType;

import java.util.List;

/**
 * Items whose identity depends on animecore data components. {@code AbstractAnimeJeiPlugin} registers a subtype
 * interpreter for every such item automatically, so each scroll / infused weapon / badge shows separately in JEI.
 */
public interface AnimeComponentItem {
    List<ComponentType<?>> subtypeComponents();
}
