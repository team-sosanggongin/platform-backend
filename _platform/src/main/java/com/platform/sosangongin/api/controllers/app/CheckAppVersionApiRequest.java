package com.platform.sosangongin.api.controllers.app;

import com.platform.sosangongin.cases.app.version.CheckAppVersionRequest;
import com.platform.sosangongin.domains.common.ClientPlatform;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckAppVersionApiRequest {
    private ClientPlatform platform;
    private String appVersion;

    public CheckAppVersionRequest toUseCaseRequest() {
        return CheckAppVersionRequest.builder()
                .platform(platform)
                .appVersion(appVersion)
                .build();
    }
}
