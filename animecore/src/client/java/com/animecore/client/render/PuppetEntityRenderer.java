package com.animecore.client.render;

import com.animecore.entity.PuppetEntity;
import com.animecore.platform.Ids;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Map;

/** Puppets reuse the vanilla biped model; only small 64x64 generated skins are shipped (one per puppet kind). */
public class PuppetEntityRenderer extends BipedEntityRenderer<PuppetEntity, BipedEntityModel<PuppetEntity>> {
    private static final Map<String, Float> SCALE = Map.of("lizard", 0.7f, "toad", 0.6f, "bee", 0.5f, "drone", 0.5f,
            "metal", 1.25f, "demon", 1.2f);

    public PuppetEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.ZOMBIE)), 0.5f);
    }

    @Override
    public Identifier getTexture(PuppetEntity entity) {
        return Ids.core("textures/entity/puppet/" + entity.kind() + ".png");
    }

    @Override
    protected void scale(PuppetEntity entity, MatrixStack matrices, float amount) {
        float s = SCALE.getOrDefault(entity.kind(), 1f);
        matrices.scale(s, s, s);
    }
}
