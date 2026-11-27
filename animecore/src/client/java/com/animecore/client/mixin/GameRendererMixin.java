package com.animecore.client.mixin;

import com.animecore.client.vfx.JuiceClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Camera shake for heavy blows (see JuiceClient), applied alongside the vanilla hurt tilt. */
@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @Inject(method = "tiltViewWhenHurt", at = @At("TAIL"))
    private void animecore$shake(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        JuiceClient.applyShake(matrices, tickDelta);
    }
}
