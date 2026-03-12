package com.platform.sosangongin.cases.auth.verification;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class PhoneVerificationRequest extends CommonRequestTemplate {
    private final boolean isPhoneVerificationRequest;
    private final String userId;
    private final String code;
}
