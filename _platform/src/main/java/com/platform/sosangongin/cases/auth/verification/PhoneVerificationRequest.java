package com.platform.sosangongin.cases.auth.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class PhoneVerificationRequest {
    private final boolean isPhoneVerificationRequest;
    private final String userId;
    private final String code;
}
