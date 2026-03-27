package com.platform.sosangongin.api.controllers.auth.token;

import com.platform.sosangongin.cases.auth.token.RefreshTokenFailureReason;
import com.platform.sosangongin.cases.auth.token.RefreshTokenResult;
import lombok.Getter;

@Getter
public class RefreshTokenApiResponse {
    private final boolean success;
    private final String accessToken;
    private final String refreshToken;
    private final RefreshTokenFailureReason failureReason;

    public RefreshTokenApiResponse(RefreshTokenResult result) {
        this.success = result.isSuccess();
        this.accessToken = result.getAccessToken();
        this.refreshToken = result.getRefreshToken();
        this.failureReason = result.getFailureReason();
    }
}
