package com.animecore.client.render;

import com.animecore.api.AnimeModContext;
import com.animecore.api.AnimeModRegistry;
import com.animecore.registry.AnimeRegistries;
import com.animecore.registry.definition.TransformationDefinition;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/** Draws a {@link FormModels.Model} in place of an entity, with procedural animation from its movement. */
public final class FormModelRenderer {
    private FormModelRenderer() {}

    /** The model a player's active form replaces them with, if any. */
    public static @Nullable FormModels.Model forPlayer(PlayerEntity player) {
        for (AnimeModContext ctx : AnimeModRegistry.all()) {
            Optional<Identifier> id = ctx.maybePower(player).flatMap(p -> p.activeTransformation())
                    .flatMap(f -> AnimeRegistries.transformation(player.getWorld().getRegistryManager(), f))
                    .map(RegistryEntry::value).map(TransformationDefinition::visuals).flatMap(TransformationDefinition.Visuals::model);
            if (id.isPresent()) return FormModels.get(id.get());
        }
        return null;
    }

    public static void render(LivingEntity entity, FormModels.Model model, float tickDelta, MatrixStack matrices,
                              VertexConsumerProvider buffers, int light) {
        render(entity, model, tickDelta, matrices, buffers, light, 0xFFFFFFFF, false);
    }

    /** {@code argb} tints the model; {@code translucent} forces the translucent layer (ghostly Stands). */
    public static void render(LivingEntity entity, FormModels.Model model, float tickDelta, MatrixStack matrices,
                              VertexConsumerProvider buffers, int light, int argb, boolean translucent) {
        float bodyYaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
        float headYaw = MathHelper.lerpAngleDegrees(tickDelta, entity.prevHeadYaw, entity.headYaw) - bodyYaw;
        float headPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
        float limbAngle = entity.limbAnimator.getPos(tickDelta);
        float limbDistance = Math.min(1f, entity.limbAnimator.getSpeed(tickDelta));
        float age = entity.age + tickDelta;
        animate(model, limbAngle, limbDistance, age, MathHelper.wrapDegrees(headYaw), headPitch);

        matrices.push();
        float s = entity.getScale();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f - bodyYaw));
        if (entity.deathTime > 0) {
            float f = Math.min(1f, MathHelper.sqrt((entity.deathTime + tickDelta - 1f) / 20f * 1.6f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 90f));
        }
        matrices.scale(-s, -s, s);
        matrices.translate(0f, -1.501f, 0f);
        int overlay = OverlayTexture.getUv(0f, entity.hurtTime > 0 || entity.deathTime > 0);
        RenderLayer layer = model.translucent() || translucent ? RenderLayer.getEntityTranslucent(model.texture()) : RenderLayer.getEntityCutoutNoCull(model.texture());
        model.root().render(matrices, buffers.getBuffer(layer), light, overlay, argb);
        matrices.pop();
    }

    private static void animate(FormModels.Model model, float limb, float dist, float age, float headYaw, float headPitch) {
        float walk = MathHelper.cos(limb * 0.6662f);
        for (FormModels.Part p : model.parts()) {
            var part = p.part();
            part.pitch = p.pitch();
            part.yaw = p.yaw();
            part.roll = p.roll();
            float a = p.amp();
            switch (p.anim()) {
                case HEAD -> {
                    part.yaw = p.yaw() + headYaw * MathHelper.RADIANS_PER_DEGREE * 0.7f;
                    part.pitch = p.pitch() + headPitch * MathHelper.RADIANS_PER_DEGREE * 0.6f;
                }
                case LEG_A -> part.pitch = p.pitch() + walk * 0.9f * dist * a;
                case LEG_B -> part.pitch = p.pitch() - walk * 0.9f * dist * a;
                case ARM_A -> {
                    part.pitch = p.pitch() + walk * 0.7f * dist * a;
                    part.roll = p.roll() + MathHelper.sin(age * 0.07f) * 0.05f;
                }
                case ARM_B -> {
                    part.pitch = p.pitch() - walk * 0.7f * dist * a;
                    part.roll = p.roll() - MathHelper.sin(age * 0.07f) * 0.05f;
                }
                case TAIL -> {
                    part.yaw = p.yaw() + MathHelper.sin(age * 0.09f + p.phase()) * 0.22f * a;
                    part.pitch = p.pitch() + MathHelper.cos(age * 0.07f + p.phase()) * 0.1f * a;
                }
                case WING -> part.roll = p.roll() + MathHelper.sin(age * 0.9f + p.phase()) * 0.25f * a;
                case JAW -> part.pitch = p.pitch() + (MathHelper.sin(age * 0.12f) + 1f) * 0.12f * a;
                case BOB -> part.pivotY = p.pivotY() + MathHelper.sin(age * 0.1f) * 0.6f * a + walk * dist * 0.5f;
                case SWAY -> part.roll = p.roll() + MathHelper.sin(age * 0.05f + p.phase()) * 0.06f * a;
                default -> { }
            }
        }
    }
}
