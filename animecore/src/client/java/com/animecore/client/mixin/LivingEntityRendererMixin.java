package com.animecore.client.mixin;

import com.animecore.client.render.FormModelRenderer;
import com.animecore.client.render.FormModels;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** A player in a form with a low-poly model (full tailed beast, Susanoo) is drawn as that model instead. */
@Mixin(LivingEntityRenderer.class)
abstract class LivingEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    private void animecore$formModel(LivingEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                                     VertexConsumerProvider buffers, int light, CallbackInfo ci) {
        if (!(entity instanceof PlayerEntity player) || player.isInvisible()) return;
        FormModels.Model model = FormModelRenderer.forPlayer(player);
        if (model == null) return;
        FormModelRenderer.render(entity, model, tickDelta, matrices, buffers, light);
        ci.cancel();
    }
}
