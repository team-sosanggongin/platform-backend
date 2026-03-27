package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.domains.common.ClientPlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class RefreshTokenRequest {

    private final String refreshToken;
    private final ClientPlatform agentType;
    private final String deviceInfo;

}
