package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@Getter
@ToString
public class LoginResult extends CommonResultTemplate {

    private final String accessToken;
    private final String refreshToken;
    private final UUID userId;

    @Builder
    public LoginResult(HttpStatus httpStatus, String message, String nextUrl, UUID userId, String accessToken, String refreshToken) {
        super(httpStatus, message, nextUrl);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
