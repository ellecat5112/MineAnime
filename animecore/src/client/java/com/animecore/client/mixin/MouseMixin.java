package com.animecore.client.mixin;

import com.animecore.client.technique.TokenClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Sneak + scroll while holding a technique token switches the technique instead of the hotbar slot. */
@Mixin(Mouse.class)
abstract class MouseMixin {
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void animecore$cycleTechnique(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (TokenClient.onScroll(vertical)) ci.cancel();
    }
}
