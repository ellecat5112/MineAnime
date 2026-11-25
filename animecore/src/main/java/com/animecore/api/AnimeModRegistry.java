package com.animecore.api;

import com.animecore.AnimeCore;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** All power systems currently loaded (one per anime content mod). */
public final class AnimeModRegistry {
    private static final Map<String, AnimeModContext> CONTEXTS = Collections.synchronizedMap(new LinkedHashMap<>());

    private AnimeModRegistry() {}

    static AnimeModContext register(AnimeModContext ctx) {
        AnimeModContext previous = CONTEXTS.putIfAbsent(ctx.modId(), ctx);
        if (previous != null) throw new IllegalStateException("Power system already registered: " + ctx.modId());
        AnimeCore.LOGGER.info("[animecore] Registered power system '{}' with pools {}", ctx.modId(),
                ctx.pools().stream().map(p -> p.id().toString()).toList());
        return ctx;
    }

    public static Optional<AnimeModContext> get(String modId) {
        return Optional.ofNullable(CONTEXTS.get(modId));
    }

    public static Collection<AnimeModContext> all() {
        synchronized (CONTEXTS) {
            return java.util.List.copyOf(CONTEXTS.values());
        }
    }
}
