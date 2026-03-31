package com.platform.sosangongin.api.controllers.consent;

import com.platform.sosangongin.cases.consent.PendingConsentResult;
import com.platform.sosangongin.domains.consent.PendingConsentVo;
import lombok.Getter;

import java.util.List;

@Getter
public class PendingConsentApiResponse {
    private final List<PendingConsentVo> pendingConsents;
    private final boolean allAgreed;

    public PendingConsentApiResponse(PendingConsentResult result) {
        this.pendingConsents = result.getPendingConsents();
        this.allAgreed = result.isAllAgreed();
    }
}
