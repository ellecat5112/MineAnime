package com.animecore;

import com.animecore.ability.AbilityExecutor;
import com.animecore.ability.action.AbilityActions;
import com.animecore.animation.AnimationService;
import com.animecore.api.event.AnimeCoreEvents;
import com.animecore.command.AnimeCoreCommands;
import com.animecore.entity.AnimeEntities;
import com.animecore.item.AnimeDataComponents;
import com.animecore.item.AnimeWeaponItem;
import com.animecore.loot.AnimeLootFunctions;
import com.animecore.network.AnimeCoreNetworking;
import com.animecore.registry.AnimeBuiltinRegistries;
import com.animecore.registry.AnimeRegistries;
import com.animecore.trait.TraitModifiers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Engine entrypoint. Contains no anime-specific content whatsoever. */
public final class AnimeCore implements ModInitializer {
    public static final String MOD_ID = "animecore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        AnimeBuiltinRegistries.init();
        AbilityActions.init();
        TraitModifiers.init();
        AnimeRegistries.init();
        AnimeEntities.init();
        AnimeDataComponents.init();
        AnimeLootFunctions.init();
        AnimeCoreNetworking.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, access, env) -> AnimeCoreCommands.register(dispatcher));
        EntityTrackingEvents.START_TRACKING.register(AnimationService::onStartTracking);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> AnimationService.onDisconnect(handler.player));

        // Reactive dodge / counter (Substitution, Z-Vanish, Flash Step): listeners first, then armed abilities.
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return true;
            if (AnimeWeaponItem.tryDeflect(player, source)) return false;
            if (AnimeCoreEvents.REACTIVE_DEFENSE.invoker().tryDefend(player, source, amount)) return false;
            return !AbilityExecutor.triggerReactive(player, source);
        });

        // Death interception (Izanagi, Senzu revival...). Health restored so the player is not left at 0 HP.
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return true;
            if (!AnimeCoreEvents.FATAL_DAMAGE_INTERCEPT.invoker().intercept(player, source, amount)) return true;
            if (player.getHealth() <= 0f) player.setHealth(1f);
            return false;
        });

        LOGGER.info("[animecore] Engine initialized");
    }
}
