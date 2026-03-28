package com.backoffice.sosangongin.errors;

public class AccountLockedException extends BackofficeBusinessError {

    public AccountLockedException(String message) {
        super(message);
    }
}