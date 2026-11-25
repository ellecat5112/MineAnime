package com.animecore.client.input;

import com.animecore.api.AnimeModContext;
import com.animecore.client.platform.AnimeClientNetworking;
import com.animecore.network.ComboInputPayload;
import com.animecore.network.HotbarCastPayload;
import com.animecore.network.TransformPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Keybind factory. Every binding gets the content mod's own category ({@code key.categories.<modId>}) and
 * translation key ({@code key.<modId>.<name>}), so two anime mods never fight over names. Press AND release are
 * forwarded (release fires CHARGED abilities).
 */
public final class AnimeKeybinds {
    private record Binding(KeyBinding key, Runnable onPress, Runnable onRelease, boolean[] down) {}

    private static final List<Binding> BINDINGS = new ArrayList<>();
    private static boolean listening;

    private AnimeKeybinds() {}

    public static KeyBinding hotbarSlot(AnimeModContext ctx, int slot, int defaultKey) {
        return register(ctx, "hotbar_" + (slot + 1), defaultKey,
                () -> AnimeClientNetworking.send(new HotbarCastPayload(ctx.modId(), slot, true)),
                () -> AnimeClientNetworking.send(new HotbarCastPayload(ctx.modId(), slot, false)));
    }

    public static KeyBinding transform(AnimeModContext ctx, int defaultKey) {
        return register(ctx, "transform", defaultKey,
                () -> AnimeClientNetworking.send(new TransformPayload(ctx.modId(), Optional.empty())), () -> {});
    }

    public static KeyBinding comboToken(AnimeModContext ctx, Identifier token, int defaultKey) {
        return register(ctx, "combo_" + token.getPath(), defaultKey,
                () -> AnimeClientNetworking.send(new ComboInputPayload(ctx.modId(), token, false)), () -> {});
    }

    public static KeyBinding comboCommit(AnimeModContext ctx, int defaultKey) {
        return register(ctx, "combo_release", defaultKey,
                () -> AnimeClientNetworking.send(new ComboInputPayload(ctx.modId(), ctx.id("release"), true)), () -> {});
    }

    public static KeyBinding register(AnimeModContext ctx, String name, int defaultKey, Runnable onPress, Runnable onRelease) {
        KeyBinding key = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(ctx.keyTranslation(name), InputUtil.Type.KEYSYM, defaultKey, ctx.keyCategory()));
        BINDINGS.add(new Binding(key, onPress, onRelease, new boolean[1]));
        ensureListening();
        return key;
    }

    private static void ensureListening() {
        if (listening) return;
        listening = true;
        ClientTickEvents.END_CLIENT_TICK.register(AnimeKeybinds::tick);
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null) return;
        boolean allowed = client.currentScreen == null;
        for (Binding b : BINDINGS) {
            boolean pressed = allowed && b.key().isPressed();
            if (pressed && !b.down()[0]) b.onPress().run();
            else if (!pressed && b.down()[0]) b.onRelease().run();
            b.down()[0] = pressed;
        }
    }
}
