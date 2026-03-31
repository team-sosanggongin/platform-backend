package com.platform.sosangongin.api.controllers.app;

import com.platform.sosangongin.cases.app.version.CheckAppVersionResult;
import lombok.Getter;

@Getter
public class CheckAppVersionApiResponse {
    private final boolean compatible;
    private final boolean forceUpdateRequired;
    private final String latestVersion;
    private final String reason;

    public CheckAppVersionApiResponse(CheckAppVersionResult result) {
        this.compatible = result.isCompatible();
        this.forceUpdateRequired = result.isForceUpdateRequired();
        this.latestVersion = result.getLatestVersion();
        this.reason = result.getReason();
    }
}
