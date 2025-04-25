package com.inspire17.ythelper.exceptions;

import lombok.Getter;

@Getter
public class ServerException extends RuntimeException {
    private final String message;
    private final int code;

    public ServerException(String message, int code) {
        this.message = message;
        this.code = code;
    }
}