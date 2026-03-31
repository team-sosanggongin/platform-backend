package com.platform.sosangongin.cases.auth.token;

public enum RefreshTokenFailureReason {
    TOKEN_EXPIRED,
    DEVICE_MISMATCH,
    TOKEN_REUSED,
    TOKEN_NOT_FOUND,
    USER_NOT_FOUND
}
