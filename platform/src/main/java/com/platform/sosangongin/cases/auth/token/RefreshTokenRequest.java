package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshTokenRequest extends CommonRequestTemplate {
    private final String refreshToken;

    @Builder
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
