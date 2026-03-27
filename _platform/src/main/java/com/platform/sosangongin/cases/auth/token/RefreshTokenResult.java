package com.platform.sosangongin.cases.auth.token;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshTokenResult {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public RefreshTokenResult(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
