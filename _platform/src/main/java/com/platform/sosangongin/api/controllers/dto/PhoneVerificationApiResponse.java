package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.CommonResultTemplate;
import com.platform.sosangongin.cases.auth.verification.PhoneVerificationResult;
import lombok.Getter;

@Getter
public class PhoneVerificationApiResponse extends CommonResultTemplate {
    public PhoneVerificationApiResponse(PhoneVerificationResult result) {
        super(result.getHttpStatus(), result.getMessage());
    }
}
