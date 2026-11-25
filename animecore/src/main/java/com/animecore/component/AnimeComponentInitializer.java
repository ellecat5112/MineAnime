package com.animecore.component;

import com.animecore.api.AnimeModContext;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

/**
 * Subclass this once per content mod and list it under the {@code cardinal-components} entrypoint:
 * <pre>{@code
 * public final class NarutoComponents extends AnimeComponentInitializer {
 *     public NarutoComponents() { super(() -> NarutoRPG.CTX); }
 * }
 * }</pre>
 */
public abstract class AnimeComponentInitializer implements EntityComponentInitializer {
    private final java.util.function.Supplier<AnimeModContext> context;

    protected AnimeComponentInitializer(java.util.function.Supplier<AnimeModContext> context) {
        this.context = context;
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        AnimeModContext ctx = context.get();
        registry.registerForPlayers(ctx.powerKey(), player -> new PlayerPowerComponent(player, ctx), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
