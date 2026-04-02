package com.backoffice.sosangongin.global.exception;

public class AccountLockedException extends BackofficeBusinessError {

    public AccountLockedException(String message) {
        super(message);
    }
}
