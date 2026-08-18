package com.sydneyarchive.auth.enums;

public enum GuestTokenFamilyStatus {
    ACTIVE,
    REVOKED,
    UNKNOWN
    ;

    public static GuestTokenFamilyStatus fromValue(String value) {
        if (value == null) return UNKNOWN;

        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
