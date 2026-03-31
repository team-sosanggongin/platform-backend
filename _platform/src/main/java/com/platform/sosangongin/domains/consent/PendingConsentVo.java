package com.platform.sosangongin.domains.consent;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PendingConsentVo {
    private final Long consentPolicyId;
    private final String title;
    private final String content;
    private final ConsentType consentType;
    private final boolean required;
    private final String version;

    public static PendingConsentVo from(ConsentPolicy policy) {
        return PendingConsentVo.builder()
                .consentPolicyId(policy.getId())
                .title(policy.getTitle())
                .content(policy.getContent())
                .consentType(policy.getConsentType())
                .required(policy.isRequired())
                .version(policy.getVersion())
                .build();
    }
}
