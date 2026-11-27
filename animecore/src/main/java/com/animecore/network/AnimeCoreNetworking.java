package com.animecore.network;

import com.animecore.ability.AbilityExecutor;
import com.animecore.api.AnimeModRegistry;
import com.animecore.movement.AnimeMovementHandler;
import com.animecore.platform.AnimeNetworking;
import com.animecore.transformation.TransformationService;

/**
 * Engine payloads. These are shared infrastructure ({@code animecore:*}, exactly one engine per modpack) and every
 * payload carries the owning system / namespaced ids, so traffic for Naruto and Bleach is routed independently.
 * Content-mod specific payloads use {@link com.animecore.api.AnimeModContext#payloadId(String)}.
 */
public final class AnimeCoreNetworking {
    private AnimeCoreNetworking() {}

    public static void init() {
        AnimeNetworking.registerS2C(AnimationSyncPayload.ID, AnimationSyncPayload.CODEC);
        AnimeNetworking.registerC2S(CastAbilityPayload.ID, CastAbilityPayload.CODEC);
        AnimeNetworking.registerC2S(HotbarCastPayload.ID, HotbarCastPayload.CODEC);
        AnimeNetworking.registerC2S(ComboInputPayload.ID, ComboInputPayload.CODEC);
        AnimeNetworking.registerC2S(TransformPayload.ID, TransformPayload.CODEC);
        AnimeNetworking.registerC2S(MovementPayloads.Toggle.ID, MovementPayloads.Toggle.CODEC);
        AnimeNetworking.registerC2S(MovementPayloads.BurstStep.ID, MovementPayloads.BurstStep.CODEC);
        AnimeNetworking.registerC2S(MovementPayloads.TransformStage.ID, MovementPayloads.TransformStage.CODEC);
        AnimeNetworking.registerS2C(MovementPayloads.Afterimage.ID, MovementPayloads.Afterimage.CODEC);
        com.animecore.vfx.AbilityVfx.init();
        com.animecore.vfx.Juice.init();
        AnimeNetworking.registerS2C(com.animecore.manifestation.TimeStopPayload.ID, com.animecore.manifestation.TimeStopPayload.CODEC);

        AnimeNetworking.registerServerReceiver(CastAbilityPayload.ID,
                (payload, player) -> AbilityExecutor.handleInput(player, payload.ability(), payload.pressed()));

        AnimeNetworking.registerServerReceiver(HotbarCastPayload.ID, (payload, player) ->
                AnimeModRegistry.get(payload.system()).ifPresent(ctx -> ctx.power(player).hotbarSlot(payload.slot())
                        .ifPresent(ability -> AbilityExecutor.handleInput(player, ability, payload.pressed()))));

        AnimeNetworking.registerServerReceiver(ComboInputPayload.ID, (payload, player) ->
                AnimeModRegistry.get(payload.system()).ifPresent(ctx ->
                        AbilityExecutor.handleComboInput(player, ctx, payload.token(), payload.commit())));

        AnimeNetworking.registerServerReceiver(TransformPayload.ID, (payload, player) ->
                AnimeModRegistry.get(payload.system()).ifPresent(ctx ->
                        TransformationService.toggle(player, ctx.power(player), payload.form().orElse(null))));

        AnimeNetworking.registerServerReceiver(MovementPayloads.Toggle.ID, (payload, player) ->
                AnimeModRegistry.get(payload.system()).ifPresent(ctx -> AnimeMovementHandler.toggle(player, ctx, payload.mode())));

        AnimeNetworking.registerServerReceiver(MovementPayloads.BurstStep.ID, (payload, player) ->
                AnimeModRegistry.get(payload.system()).ifPresent(ctx ->
                        AnimeMovementHandler.burstStep(player, ctx, payload.forward(), payload.strafe())));

        AnimeNetworking.registerServerReceiver(MovementPayloads.TransformStage.ID, (payload, player) ->
                AnimeModRegistry.get(payload.system()).ifPresent(ctx -> {
                    if (payload.direction() > 0) TransformationService.ascend(player, ctx.power(player));
                    else TransformationService.descend(player, ctx.power(player));
                }));
    }
}
