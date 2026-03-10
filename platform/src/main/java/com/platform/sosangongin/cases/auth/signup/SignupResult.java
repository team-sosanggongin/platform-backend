package com.platform.sosangongin.cases.auth.signup;

import com.platform.sosangongin.cases.CommonResultTemplate;
import org.springframework.http.HttpStatus;

public class SignupResult extends CommonResultTemplate {
    public SignupResult(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
