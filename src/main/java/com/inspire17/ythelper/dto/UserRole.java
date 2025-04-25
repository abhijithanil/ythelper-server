package com.inspire17.ythelper.dto;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    SUPER_ADMIN("SUPER_ADMIN"),
    USERS("USERS"),
    EDITORS("EDITORS");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public static UserRole fromString(String role) {
        role = role.toUpperCase();
        for (UserRole userRole : UserRole.values()) {
            if (userRole.role.equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }
}
