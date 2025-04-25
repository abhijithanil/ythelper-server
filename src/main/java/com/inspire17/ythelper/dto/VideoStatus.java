package com.inspire17.ythelper.dto;

public enum VideoStatus {
    UPLOADED("UPLOADED"), // need to remove
    TODO("TODO"),
    IN_PROGRESS("IN PROGRESS"),
    IN_REVIEW("IN REVIEW"),
    APPROVED("APPROVED"),
    PUBLISHED("PUBLISHED");


    private final String status;

    VideoStatus(String status) {
        this.status = status;
    }

    public static VideoStatus fromString(String status) {
        status = status.toUpperCase();
        for (VideoStatus videoStatus : VideoStatus.values()) {
            if (videoStatus.status.equalsIgnoreCase(status)) {
                return videoStatus;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + status);
    }

}
