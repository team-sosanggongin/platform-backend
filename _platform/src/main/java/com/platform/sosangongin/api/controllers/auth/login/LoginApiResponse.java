package com.platform.sosangongin.api.controllers.auth.login;

import com.platform.sosangongin.cases.auth.login.LoginResult;
import lombok.Getter;

@Getter
public class LoginApiResponse {
    private final String accessToken;
    private final String refreshToken;

    public LoginApiResponse(LoginResult result) {
        this.accessToken = result.accessToken();
        this.refreshToken = result.refreshToken();
    }
}
