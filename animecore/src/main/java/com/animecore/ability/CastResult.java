package com.animecore.ability;

public enum CastResult {
    SUCCESS, CHARGING, ARMED, UNKNOWN_ABILITY, UNKNOWN_SYSTEM, LOCKED, TIER_TOO_LOW, WRONG_FORM,
    ON_COOLDOWN, NO_ENERGY, CANCELLED, NO_MATCH;

    public boolean ok() { return this == SUCCESS || this == CHARGING || this == ARMED; }

    public String translationKey() { return "message.animecore.cast." + name().toLowerCase(java.util.Locale.ROOT); }
}
