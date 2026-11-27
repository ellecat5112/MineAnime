package com.animecore.client.mixin;

import com.animecore.client.vfx.TimeStopClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Stopped time on the client: frozen entities do not tick (no client-side projectile drift, no walking animation). */
@Mixin(ClientWorld.class)
abstract class ClientWorldMixin {
    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    private void animecore$stoppedTime(Entity entity, CallbackInfo ci) {
        if (TimeStopClient.frozen(entity)) ci.cancel();
    }
}
