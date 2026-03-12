package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class LoginResult extends CommonResultTemplate {

    private final String accessToken;
    private final String refreshToken;

    @Builder
    public LoginResult(HttpStatus httpStatus, String message, String accessToken, String refreshToken) {
        super(httpStatus, message);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
