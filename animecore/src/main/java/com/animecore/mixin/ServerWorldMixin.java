package com.animecore.mixin;

import com.animecore.manifestation.TimeStopManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Stopped time: entities held by a {@link TimeStopManager} stop do not tick (players are held through attributes instead). */
@Mixin(ServerWorld.class)
abstract class ServerWorldMixin {
    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    private void animecore$stoppedTime(Entity entity, CallbackInfo ci) {
        if (TimeStopManager.active() && !(entity instanceof PlayerEntity) && TimeStopManager.isFrozen(entity)) ci.cancel();
    }
}
