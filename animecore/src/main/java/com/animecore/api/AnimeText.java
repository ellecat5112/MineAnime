package com.animecore.api;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * Engine messages in each content mod's own vocabulary. The engine's default wording is neutral ("technique",
 * "faction", "lineage", "energy"); a mod that registers an override (see {@link AnimeModContext.Builder#overrideText})
 * gets its own key instead, e.g. {@code message.animecore.mastery.learned} -> {@code message.narutorpg.mastery.learned}
 * ("Jutsu mastered: %s"). Resolved on the server per mod, so several anime mods can run side by side.
 */
public final class AnimeText {
    private AnimeText() {}

    /** The key {@code system} uses for {@code engineKey}. */
    public static String key(String system, String engineKey) {
        if (system == null) return engineKey;
        AnimeModContext ctx = AnimeModRegistry.get(system).orElse(null);
        if (ctx == null || !ctx.overridesText(engineKey)) return engineKey;
        return engineKey.replace(".animecore.", "." + system + ".");
    }

    public static MutableText tr(String system, String engineKey, Object... args) {
        return Text.translatable(key(system, engineKey), args);
    }

    public static MutableText tr(AnimeModContext ctx, String engineKey, Object... args) {
        return tr(ctx == null ? null : ctx.modId(), engineKey, args);
    }
}
