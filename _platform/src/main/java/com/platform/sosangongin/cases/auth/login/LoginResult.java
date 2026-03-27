package com.platform.sosangongin.cases.auth.login;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class LoginResult {

    private final String accessToken;
    private final String refreshToken;
    private final UUID userId;

    @Builder
    public LoginResult(UUID userId, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
