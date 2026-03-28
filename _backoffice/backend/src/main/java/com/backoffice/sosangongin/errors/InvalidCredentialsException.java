package com.backoffice.sosangongin.errors;

public class InvalidCredentialsException extends BackofficeBusinessError {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}