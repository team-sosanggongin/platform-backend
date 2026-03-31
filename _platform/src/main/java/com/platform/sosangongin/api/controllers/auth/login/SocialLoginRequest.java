package com.platform.sosangongin.api.controllers.auth.login;

import com.platform.sosangongin.domains.common.ClientPlatform;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginRequest {
    private final String code;
    private final String provider;
    private final ClientPlatform agentType;
    private final String deviceInfo;
}
