package com.narutorpg;

import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * NarutoRPG's wording for engine messages (assets/narutorpg/engine_text.json: engine key -> text): "Jutsu mastered",
 * "village", "clan", "missing-nin"... The engine shows these to NarutoRPG players under {@code *.narutorpg.*} keys
 * (see AnimeText); other anime mods keep the engine's neutral wording.
 */
public final class NarutoText {
    private NarutoText() {}

    public static final Map<String, String> ENGINE = load();

    private static Map<String, String> load() {
        Map<String, String> out = new LinkedHashMap<>();
        try (Reader r = new InputStreamReader(NarutoText.class.getResourceAsStream("/assets/narutorpg/engine_text.json"), StandardCharsets.UTF_8)) {
            JsonParser.parseReader(r).getAsJsonObject().entrySet().forEach(e -> out.put(e.getKey(), e.getValue().getAsString()));
        } catch (Exception e) {
            throw new IllegalStateException("assets/narutorpg/engine_text.json missing", e);
        }
        return out;
    }

    /** The NarutoRPG key for an engine key. */
    public static String key(String engineKey) { return engineKey.replace(".animecore.", ".narutorpg."); }
}
