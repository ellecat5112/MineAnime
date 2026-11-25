package com.animecore.client.animation;

import com.animecore.AnimeCore;
import com.animecore.animation.AnimLayer;
import com.animecore.network.AnimationSyncPayload;
import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.api.layered.modifier.SpeedModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Ease;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Owns one {@link ModifierLayer} per {@link AnimLayer} on every {@link AbstractClientPlayerEntity}:
 * BASE (stances, flight, wall-walk) < UPPER_BODY (hand signs, charge, beam hold) < OVERLAY (one-shot reactions).
 */
public final class AnimationLayerManager {
    private static final String[] LEG_PARTS = {"rightLeg", "leftLeg"};
    private static final Map<KeyframeAnimation, KeyframeAnimation> UPPER_BODY_CACHE = new WeakHashMap<>();
    private static final Set<Identifier> WARNED = new HashSet<>();

    private AnimationLayerManager() {}

    public static void init() {
        for (AnimLayer layer : AnimLayer.values()) {
            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(layer.id(), layer.priority(), player -> new ModifierLayer<>());
        }
    }

    @SuppressWarnings("unchecked")
    public static @Nullable ModifierLayer<IAnimation> layer(AbstractClientPlayerEntity player, AnimLayer layer) {
        IAnimation anim = PlayerAnimationAccess.getPlayerAssociatedData(player).get(layer.id());
        return anim instanceof ModifierLayer<?> modifierLayer ? (ModifierLayer<IAnimation>) modifierLayer : null;
    }

    public static void apply(AbstractClientPlayerEntity player, AnimationSyncPayload payload) {
        ModifierLayer<IAnimation> layer = layer(player, payload.layer());
        if (layer == null) return;
        if (payload.animation().isEmpty()) {
            replace(layer, null, payload.fadeTicks());
            return;
        }
        Identifier id = payload.animation().get();
        IPlayable playable = PlayerAnimationRegistry.getAnimation(id);
        if (playable == null) {
            if (WARNED.add(id)) AnimeCore.LOGGER.warn("[animecore] Missing player animation '{}'", id);
            return;
        }
        IAnimation animation;
        if (playable instanceof KeyframeAnimation keyframes) {
            animation = new KeyframeAnimationPlayer(payload.upperBodyOnly() ? upperBody(keyframes) : keyframes);
        } else {
            animation = playable.playAnimation();
        }
        if (payload.speed() != 1f) animation = new ModifierLayer<>(animation, new SpeedModifier(payload.speed()));
        replace(layer, animation, payload.fadeTicks());
    }

    public static void stop(AbstractClientPlayerEntity player, AnimLayer layer, int fadeTicks) {
        ModifierLayer<IAnimation> l = layer(player, layer);
        if (l != null) replace(l, null, fadeTicks);
    }

    private static void replace(ModifierLayer<IAnimation> layer, @Nullable IAnimation animation, int fadeTicks) {
        if (fadeTicks > 0) layer.replaceAnimationWithFade(AbstractFadeModifier.standardFadeIn(fadeTicks, Ease.INOUTSINE), animation);
        else layer.setAnimation(animation);
    }

    /** Copy with leg tracks disabled so the vanilla walk cycle keeps playing underneath. */
    private static KeyframeAnimation upperBody(KeyframeAnimation source) {
        return UPPER_BODY_CACHE.computeIfAbsent(source, src -> {
            KeyframeAnimation.AnimationBuilder builder = src.mutableCopy();
            for (String part : LEG_PARTS) {
                KeyframeAnimation.StateCollection state = builder.getPart(part);
                if (state != null) state.setEnabled(false);
            }
            return builder.build();
        });
    }
}
