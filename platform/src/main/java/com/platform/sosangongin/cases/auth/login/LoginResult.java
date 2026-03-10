package com.platform.sosangongin.cases.auth.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class LoginResult {
    private final HttpStatus httpStatus;
}
