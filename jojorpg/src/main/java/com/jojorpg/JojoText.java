package com.jojorpg;

import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JoJoRPG's wording for engine messages (assets/jojorpg/engine_text.json: engine key -> text): "fighting style" where the
 * engine says "lineage", "the Ripple" for affinities. The engine shows these to JoJoRPG players under {@code *.jojorpg.*}
 * keys (see AnimeText); other anime mods keep their own wording.
 */
public final class JojoText {
    private JojoText() {}

    public static final Map<String, String> ENGINE = load();

    private static Map<String, String> load() {
        Map<String, String> out = new LinkedHashMap<>();
        try (Reader r = new InputStreamReader(JojoText.class.getResourceAsStream("/assets/jojorpg/engine_text.json"), StandardCharsets.UTF_8)) {
            JsonParser.parseReader(r).getAsJsonObject().entrySet().forEach(e -> out.put(e.getKey(), e.getValue().getAsString()));
        } catch (Exception e) {
            throw new IllegalStateException("assets/jojorpg/engine_text.json missing", e);
        }
        return out;
    }

    /** The JoJoRPG key for an engine key. */
    public static String key(String engineKey) { return engineKey.replace(".animecore.", ".jojorpg."); }
}
