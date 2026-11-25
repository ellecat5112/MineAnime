package com.animecore.api;

import net.minecraft.util.Identifier;

/**
 * Declares one energy pool of a power system.
 *
 * @param role  PRIMARY (Chakra / Ki / Reiatsu / Cursed Energy / Haki), SECONDARY (Stamina) or EXTRA (Senjutsu, Bijuu)
 * @param color RGB used by the HUD and procedural aura
 */
public record EnergyPoolSpec(Identifier id, Role role, float baseMax, float regenPerTick, int color) {
    public enum Role { PRIMARY, SECONDARY, EXTRA }
}
