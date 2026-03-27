package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.auth.token.RefreshTokenRequest;
import com.platform.sosangongin.domains.common.ClientPlatform;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenApiRequest {
    private String refreshToken;
    private ClientPlatform agentType;
    private String deviceInfo;

    public RefreshTokenRequest toUseCaseRequest() {
        return RefreshTokenRequest.builder()
                .refreshToken(this.refreshToken)
                .agentType(this.agentType)
                .deviceInfo(this.deviceInfo)
                .build();
    }
}
