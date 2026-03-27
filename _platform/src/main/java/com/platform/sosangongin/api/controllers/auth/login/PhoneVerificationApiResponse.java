package com.platform.sosangongin.api.controllers.auth.login;

import com.platform.sosangongin.cases.auth.verification.PhoneVerificationResult;
import lombok.Getter;

@Getter
public class PhoneVerificationApiResponse {
    public PhoneVerificationApiResponse(PhoneVerificationResult result) {
    }
}
