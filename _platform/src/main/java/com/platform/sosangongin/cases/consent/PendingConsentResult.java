package com.platform.sosangongin.cases.consent;

import com.platform.sosangongin.domains.consent.PendingConsentVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PendingConsentResult {
    private final List<PendingConsentVo> pendingConsents;
    private final boolean allAgreed;
}
