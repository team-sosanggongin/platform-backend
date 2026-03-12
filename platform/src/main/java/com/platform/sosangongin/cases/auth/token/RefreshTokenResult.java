package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RefreshTokenResult extends CommonResultTemplate {
    private final String accessToken;
    private final String refreshToken;

    @Builder
    public RefreshTokenResult(HttpStatus httpStatus, String message, String accessToken, String refreshToken) {
        super(httpStatus, message);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
