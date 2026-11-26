package com.animecore.client.render;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.platform.Ids;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.TransformationDefinition;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

/**
 * Translucent shroud over a transformed player (jinchūriki chakra cloaks, curse-mark skin, Kāma lines, Sage Mode glow):
 * the player model drawn again, slightly inflated and pulsing, tinted with the active form's {@code cloak_color}.
 */
public final class ChakraCloakFeature extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final Identifier TEXTURE = Ids.core("textures/entity/story_npc/skin.png");

    private ChakraCloakFeature(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> ctx) {
        super(ctx);
    }

    public static void register() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((type, renderer, helper, ctx) -> {
            if (renderer instanceof PlayerEntityRenderer player) helper.register(new ChakraCloakFeature(player));
        });
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider buffers, int light, AbstractClientPlayerEntity player,
                       float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (player.isInvisible()) return;
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            Optional<TransformationDefinition.Visuals> v = ctx.maybePower(player).flatMap(p -> p.activeTransformation())
                    .flatMap(id -> AnimeRegistries.transformation(player.getWorld().getRegistryManager(), id))
                    .map(RegistryEntry::value).map(TransformationDefinition::visuals);
            if (v.isEmpty() || v.get().cloakColor().isEmpty()) continue;
            float pulse = 0.85f + 0.15f * MathHelper.sin(animationProgress * 0.25f);
            int alpha = MathHelper.clamp((int) (255 * v.get().cloakAlpha() * pulse), 0, 255);
            int argb = (alpha << 24) | (v.get().cloakColor().get() & 0xFFFFFF);
            matrices.push();
            matrices.scale(1.07f, 1.04f, 1.07f);
            matrices.translate(0, -0.03, 0);
            getContextModel().render(matrices, buffers.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE)),
                    0xF000F0, OverlayTexture.DEFAULT_UV, argb);
            matrices.pop();
            return;
        }
    }
}
