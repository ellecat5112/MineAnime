package com.animecore.animation;

/**
 * @param persistent re-sent to peers that start tracking the player later (looping stances), Essential P2P safe
 */
public record AnimPlayback(int fadeTicks, float speed, boolean upperBodyOnly, boolean persistent) {
    public static final AnimPlayback ONE_SHOT = new AnimPlayback(3, 1f, false, false);
    public static final AnimPlayback UPPER_BODY = new AnimPlayback(2, 1f, true, false);
    public static final AnimPlayback UPPER_BODY_HOLD = new AnimPlayback(3, 1f, true, true);
    public static final AnimPlayback STANCE = new AnimPlayback(6, 1f, false, true);
}
