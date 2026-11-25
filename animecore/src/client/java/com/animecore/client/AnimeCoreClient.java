package com.animecore.client;

import com.animecore.client.animation.AnimationLayerManager;
import com.animecore.client.hud.AnimeHudLayoutManager;
import com.animecore.client.render.AfterimageEffects;
import com.animecore.client.render.ProceduralAuraRenderer;
import com.animecore.client.network.AnimeClientPayloadHandlers;
import com.animecore.entity.AnimeEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;

/** Client entrypoint. All {@code MinecraftClient} usage is confined to {@code com.animecore.client.*}. */
public final class AnimeCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AnimationLayerManager.init();
        AnimeClientPayloadHandlers.init();
        AnimeHudLayoutManager.init();
        ProceduralAuraRenderer.init();
        AfterimageEffects.init();
        // Energy projectiles are drawn entirely by their particle trail: zero texture bytes.
        EntityRendererRegistry.register(AnimeEntities.ENERGY_PROJECTILE, EmptyEntityRenderer::new);
    }
}
