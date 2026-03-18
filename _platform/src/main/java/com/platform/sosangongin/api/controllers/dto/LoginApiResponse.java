package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.CommonResultTemplate;
import com.platform.sosangongin.cases.auth.login.LoginResult;
import lombok.Getter;

@Getter
public class LoginApiResponse extends CommonResultTemplate {
    private final String accessToken;
    private final String refreshToken;

    public LoginApiResponse(LoginResult result) {
        super(result.getHttpStatus(), result.getMessage());
        this.accessToken = result.getAccessToken();
        this.refreshToken = result.getRefreshToken();
    }
}
