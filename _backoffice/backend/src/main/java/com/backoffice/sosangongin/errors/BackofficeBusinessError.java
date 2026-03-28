package com.backoffice.sosangongin.errors;

public class BackofficeBusinessError extends RuntimeException {

    public BackofficeBusinessError(String message) {
        super(message);
    }

    public BackofficeBusinessError(String message, Throwable cause) {
        super(message, cause);
    }
}
