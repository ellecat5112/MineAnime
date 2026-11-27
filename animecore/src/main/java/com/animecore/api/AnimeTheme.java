package com.animecore.api;

import org.jetbrains.annotations.Nullable;

/**
 * How a content mod looks everywhere the engine draws for it: HUD panels, title cards, arc banners, battle glyphs.
 *
 * @param accent    main colour (HUD frame, title card slash, rank badge)
 * @param secondary second colour (title text, highlights)
 * @param glyph     a character that floats around fighters with their power out and around bosses ("ゴ" - JoJo's
 *                  menacing), or null for none
 * @param arcEndKey translation key of the arc-end banner ("To Be Continued" / "Arc Complete")
 * @param arrow     whether the arc-end banner carries the JoJo-style arrow
 */
public record AnimeTheme(int accent, int secondary, @Nullable String glyph, String arcEndKey, boolean arrow) {
    public static final AnimeTheme DEFAULT = new AnimeTheme(0x7E57C2, 0xFFD54F, null, "card.animecore.arc_end", false);
}
