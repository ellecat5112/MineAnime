package com.animecore.mixin;

import com.animecore.clan.ClanTraitHooks;
import com.animecore.movement.AnimeMovementHandler;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * SURFACE_ADHESION: walls behave like ladders while pushing into them (run up at 0.2 b/t, sneak to cling),
 * and water is solid ground like for striders (sneak to dive). Runs on both sides so prediction matches.
 */
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
    /**
     * Clan skill: kill experience becomes clan skill experience while the killer holds their clan's skill item.
     * Mirrors vanilla's own guards (doMobLoot / player-kill conditions live in getXpToDrop and shouldDropXp).
     */
    @Inject(method = "dropXp", at = @At("HEAD"), cancellable = true)
    private void animecore$clanSkillXp(@Nullable Entity attacker, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (attacker == null || self.getWorld().isClient() || self.isExperienceDroppingDisabled() || !self.shouldDropXp()) return;
        if (ClanTraitHooks.convertKillExperience(self, attacker)) ci.cancel();
    }

    @Inject(method = "isClimbing", at = @At("HEAD"), cancellable = true)
    private void animecore$wallWalk(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PlayerEntity player && player.horizontalCollision && AnimeMovementHandler.isAdhering(player)
                && (!AnimeMovementHandler.isPassive(player) || player.getPitch() < -15f)) {
            cir.setReturnValue(true);   // passive mode: look up and jump at the wall to start climbing
        }
    }

    @Inject(method = "canWalkOnFluid", at = @At("HEAD"), cancellable = true)
    private void animecore$waterWalk(FluidState state, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof PlayerEntity player && state.isIn(FluidTags.WATER) && !player.isSneaking()
                && AnimeMovementHandler.isAdhering(player)) {
            cir.setReturnValue(true);
        }
    }
}
