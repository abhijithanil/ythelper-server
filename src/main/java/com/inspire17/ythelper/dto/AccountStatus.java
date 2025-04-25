package com.inspire17.ythelper.dto;

public enum AccountStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING,
    DELETED;

    public static AccountStatus fromString(String status) {
        try {
            return AccountStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account status: " + status);
        }
    }
}
