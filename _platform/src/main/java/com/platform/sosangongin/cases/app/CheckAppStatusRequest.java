package com.platform.sosangongin.cases.app;

import com.platform.sosangongin.domains.common.ClientPlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CheckAppStatusRequest {
    private final ClientPlatform platform;
    private final int versionCode;
}
