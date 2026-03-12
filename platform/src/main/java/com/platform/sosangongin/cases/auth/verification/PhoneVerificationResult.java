package com.platform.sosangongin.cases.auth.verification;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class PhoneVerificationResult extends CommonResultTemplate {
    @Builder
    public PhoneVerificationResult(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }
}
