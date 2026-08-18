package com.sydneyarchive.auth.enums;

public enum Platform {

    WEB,
    MOBILE,
    NONE,
    ;

    public static Platform fromString(String value) {
        if (value == null) return NONE;

        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
