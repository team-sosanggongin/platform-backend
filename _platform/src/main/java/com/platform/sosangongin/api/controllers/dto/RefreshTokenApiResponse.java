package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.CommonResultTemplate;
import com.platform.sosangongin.cases.auth.token.RefreshTokenResult;
import lombok.Getter;

@Getter
public class RefreshTokenApiResponse extends CommonResultTemplate {
    private final String accessToken;
    private final String refreshToken;

    public RefreshTokenApiResponse(RefreshTokenResult result) {
        super(result.getHttpStatus(), result.getMessage());
        this.accessToken = result.getAccessToken();
        this.refreshToken = result.getRefreshToken();
    }
}
