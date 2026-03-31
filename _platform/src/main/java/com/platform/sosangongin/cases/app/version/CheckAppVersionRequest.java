package com.platform.sosangongin.cases.app.version;

import com.platform.sosangongin.domains.common.ClientPlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CheckAppVersionRequest {
    private final ClientPlatform platform;
    private final String appVersion;
}
