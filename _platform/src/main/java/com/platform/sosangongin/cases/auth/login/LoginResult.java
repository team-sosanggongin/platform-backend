package com.platform.sosangongin.cases.auth.login;


import lombok.Builder;

@Builder
public record LoginResult(String accessToken, String refreshToken, String message) {
}
