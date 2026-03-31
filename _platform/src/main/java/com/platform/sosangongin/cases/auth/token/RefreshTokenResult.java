package com.platform.sosangongin.cases.auth.token;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshTokenResult {
    private final boolean success;
    private final String accessToken;
    private final String refreshToken;

    private final RefreshTokenFailureReason failureReason;

    @Builder
    public RefreshTokenResult(boolean success, String accessToken, String refreshToken, RefreshTokenFailureReason failureReason) {
        this.success = success;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.failureReason = failureReason;
    }
}
