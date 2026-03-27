package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.auth.token.RefreshTokenResult;
import lombok.Getter;

@Getter
public class RefreshTokenApiResponse {
    private final String accessToken;
    private final String refreshToken;

    public RefreshTokenApiResponse(RefreshTokenResult result) {
        this.accessToken = result.getAccessToken();
        this.refreshToken = result.getRefreshToken();
    }
}
