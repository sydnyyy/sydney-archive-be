package com.wishlist.global.redis.constant;

public final class WebSocketRedisKeys {

    public static final String WEBSOCKET_SESSION_MAIN_KEY_PREFIX = "WS:MAIN:";
    public static final String WEBSOCKET_SESSION_TERMINATE_SIGNAL_KEY_PREFIX = "WS:TERMINATE_SIGNAL:";

    public static final String WEBSOCKET_SESSION_TERMINATE_CHECK_ZSET = "TERMINATE_CHECK_CLIENT_IDS";
    public static final String WEBSOCKET_SESSION_TERMINATE_STREAM = "WS:TERMINATE_SESSION_STREAM";
}
