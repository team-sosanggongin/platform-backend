package com.platform.sosangongin.api.controllers.consent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgreeConsentApiRequest {
    private Long consentPolicyId;
    private boolean agreed;
}
