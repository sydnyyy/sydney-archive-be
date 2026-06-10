package com.theforbiddenland.common.entity;

public record Permission(
        boolean canEdit,
        boolean canDelete
) {

    public static Permission of(boolean isAdmin) {
        return new Permission(isAdmin, isAdmin);
    }
}
