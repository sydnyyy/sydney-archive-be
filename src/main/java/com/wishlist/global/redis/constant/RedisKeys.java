package com.wishlist.global.redis.constant;

public final class RedisKeys {

    public static final String WS_SESSION_MAIN_KEY_PREFIX = "WS:MAIN:";
    public static final String WS_TERMINATE_SIGNAL_KEY_PREFIX = "WS:TERMINATE_SIGNAL:";

    public static final String WS_SESSION_TERMINATE_CHECK_ZSET = "WS:TERMINATE_CHECK_CLIENT_IDS";

    public static final String WS_SESSION_TERMINATE_STREAM = "BROADCAST:WS:TERMINATE_SESSION";
    public static final String KEY_USER_ACTIVITY_STREAM = "BROADCAST:USER_ACTIVITY";
}
