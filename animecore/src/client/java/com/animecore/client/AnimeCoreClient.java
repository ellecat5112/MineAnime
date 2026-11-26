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
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.RenderLayer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import com.animecore.client.render.PuppetEntityRenderer;
import com.animecore.ward.WardBlocks;

/** Client entrypoint. All {@code MinecraftClient} usage is confined to {@code com.animecore.client.*}. */
public final class AnimeCoreClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AnimationLayerManager.init();
        AnimeClientPayloadHandlers.init();
        AnimeHudLayoutManager.init();
        ProceduralAuraRenderer.init();
        AfterimageEffects.init();
        // Energy projectiles: procedural swirling elemental balls (zero texture bytes).
        EntityRendererRegistry.register(AnimeEntities.ENERGY_PROJECTILE, com.animecore.client.vfx.EnergyProjectileRenderer::new);
        // Thrown ninja tools render as their own item sprite; puppets reuse the biped model.
        EntityRendererRegistry.register(AnimeEntities.THROWN_TOOL, ctx -> new FlyingItemEntityRenderer<>(ctx, 1.0f, true));
        EntityRendererRegistry.register(AnimeEntities.PUPPET, PuppetEntityRenderer::new);
        EntityRendererRegistry.register(AnimeEntities.STORY_NPC, com.animecore.client.render.StoryNpcRenderer::new);
        com.animecore.client.quest.QuestClient.init();
        com.animecore.client.render.ChakraCloakFeature.register();
        BlockRenderLayerMap.INSTANCE.putBlock(WardBlocks.CHAKRA_WALL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(WardBlocks.BARRIER_GATE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(WardBlocks.WARD_TAG, RenderLayer.getCutout());
        com.animecore.client.render.ClanItemColors.register();
        com.animecore.client.vfx.VfxManager.init();
        com.animecore.client.origin.OriginClient.init();
        com.animecore.client.technique.TokenClient.init();
    }
}
