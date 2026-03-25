package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.app.CheckAppStatusRequest;
import com.platform.sosangongin.domains.common.ClientPlatform;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CheckAppStatusApiRequest {
    private ClientPlatform platform;
    private int versionCode;

    public CheckAppStatusRequest toUseCaseRequest() {
        return CheckAppStatusRequest.builder()
                .platform(platform)
                .versionCode(versionCode)
                .build();
    }
}
