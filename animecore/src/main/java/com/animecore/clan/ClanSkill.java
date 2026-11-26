package com.animecore.clan;

/** Clan skill experience curve (shared by every system). */
public final class ClanSkill {
    private ClanSkill() {}

    /** Experience needed to go from {@code level} to {@code level + 1}: 14 at level 1, ~73 at 10, ~440 at 49. */
    public static long experienceForNext(int level) {
        return Math.round(10 + 4 * Math.pow(Math.max(1, level), 1.2));
    }

    public static float progress(int level, long xp, int maxLevel) {
        if (level >= maxLevel) return 1f;
        return Math.min(1f, xp / (float) experienceForNext(level));
    }
}
