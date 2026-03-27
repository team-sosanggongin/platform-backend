package com.platform.sosangongin.cases.app.version;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckAppVersionResult {
    private final boolean compatible;
    private final boolean forceUpdateRequired;
    private final String latestVersion;
    private final String reason;
}
