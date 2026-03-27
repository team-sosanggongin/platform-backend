package com.platform.sosangongin.api.controllers.auth.login;

import com.platform.sosangongin.cases.auth.verification.PhoneVerificationRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PhoneVerificationApiRequest {
    private boolean isPhoneVerificationRequest;
    private String userId;
    private String code;

    public PhoneVerificationRequest toUseCaseRequest() {
        return new PhoneVerificationRequest(isPhoneVerificationRequest, userId, code);
    }
}
