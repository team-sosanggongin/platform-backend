package com.backoffice.sosangongin.errors;

public class PermissionDeniedException extends BackofficeBusinessError {

    public PermissionDeniedException(String message) {
        super(message);
    }
}