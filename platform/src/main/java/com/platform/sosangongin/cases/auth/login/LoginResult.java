package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class LoginResult extends CommonResultTemplate {

    @Builder
    public LoginResult(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
