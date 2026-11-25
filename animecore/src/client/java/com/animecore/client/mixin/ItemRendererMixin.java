package com.animecore.client.mixin;

import com.animecore.client.render.HeldModels;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Swaps flat item models for registered low-poly 3D models outside of GUIs. */
@Mixin(ItemRenderer.class)
abstract class ItemRendererMixin {
    @ModifyVariable(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"), argsOnly = true)
    private BakedModel animecore$heldModel(BakedModel model, ItemStack stack, ModelTransformationMode mode, boolean leftHanded,
                                          MatrixStack matrices, VertexConsumerProvider consumers, int light, int overlay) {
        if (stack.isEmpty()) return model;
        BakedModel swapped = HeldModels.swap(stack.getItem(), mode);
        return swapped != null ? swapped : model;
    }
}
