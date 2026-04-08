package com.backoffice.sosangongin.global.exception;

public class ValidationFailedException extends BackofficeBusinessError {

    public ValidationFailedException(String message) {
        super(message);
    }
}