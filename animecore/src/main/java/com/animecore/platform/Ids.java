package com.animecore.platform;

import com.animecore.AnimeCore;
import net.minecraft.util.Identifier;

import java.util.Optional;

/**
 * Version shield for {@link Identifier} construction.
 * <p>1.21 replaced {@code new Identifier(..)} with {@code Identifier.of(..)}; any future rename only touches this class.
 */
public final class Ids {
    private Ids() {}

    public static Identifier of(String namespace, String path) {
        return Identifier.of(namespace, path);
    }

    /** Parses {@code "ns:path"} (defaults to {@code minecraft:} when no namespace). Throws on invalid input. */
    public static Identifier parse(String full) {
        return Identifier.of(full);
    }

    public static Optional<Identifier> tryParse(String full) {
        return Optional.ofNullable(Identifier.tryParse(full));
    }

    /** Identifier in the {@code animecore} namespace. Reserved for engine-owned shared infrastructure only. */
    public static Identifier core(String path) {
        return of(AnimeCore.MOD_ID, path);
    }

    /** {@code type.namespace.path}, e.g. {@code ability.narutorpg.fireball_jutsu}. */
    public static String translationKey(String type, Identifier id) {
        return type + "." + id.getNamespace() + "." + id.getPath().replace('/', '.');
    }
}
