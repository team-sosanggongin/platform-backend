package com.platform.sosangongin.cases.consent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AgreeConsentRequest {
    private final String userId;
    private final Long consentPolicyId;
    private final boolean agreed;
}
