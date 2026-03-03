package com.theforbiddenland.auth.constants;

public final class AuthConstants {
    private AuthConstants() {}

    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_HEADER_PREFIX = "Bearer ";
    public static final String COOKIE_REFRESH_TOKEN = "refresh_token";

    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60 * 2;
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 14;

}
