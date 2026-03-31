package com.platform.sosangongin.api.controllers.consent;

import com.platform.sosangongin.cases.consent.AgreeConsentResult;
import lombok.Getter;

@Getter
public class AgreeConsentApiResponse {
    private final boolean success;

    public AgreeConsentApiResponse(AgreeConsentResult result) {
        this.success = result.isSuccess();
    }
}
