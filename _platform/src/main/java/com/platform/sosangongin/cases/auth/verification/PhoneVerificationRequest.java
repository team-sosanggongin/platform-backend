package com.platform.sosangongin.cases.auth.verification;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class PhoneVerificationRequest extends CommonRequestTemplate {
    private final boolean isPhoneVerificationRequest;
    private final String userId;
    private final String code;
}
