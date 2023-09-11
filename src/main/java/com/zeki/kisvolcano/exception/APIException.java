package com.zeki.kisvolcano.exception;

import lombok.Getter;


@Getter
public class APIException extends RuntimeException {
    private final ResponseCode responseCode;
    private final String message;

    // Custom message constructor
    public APIException(ResponseCode responseCode, String message) {
        this.responseCode = responseCode;
        this.message = message;
    }

    // Default message constructor
    public APIException(ResponseCode responseCode) {
        this.responseCode = responseCode;
        this.message = responseCode.getDefaultMessage();
    }

}
