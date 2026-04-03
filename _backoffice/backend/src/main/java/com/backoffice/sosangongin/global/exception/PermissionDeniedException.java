package com.backoffice.sosangongin.global.exception;

public class PermissionDeniedException extends BackofficeBusinessError {

    public PermissionDeniedException(String message) {
        super(message);
    }
}
