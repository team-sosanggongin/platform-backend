package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.auth.login.LoginRequest;
import com.platform.sosangongin.domains.common.ClientPlatform;
import com.platform.sosangongin.domains.user.SocialProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginApiRequest {
    private String code;
    private String provider;
    private ClientPlatform agentType;
    private String deviceInfo;

    public LoginRequest toUseCaseRequest() {
        return LoginRequest.builder()
                .code(code)
                .provider(SocialProvider.valueOf(provider.toUpperCase()))
                .agentType(agentType)
                .deviceInfo(deviceInfo)
                .build();
    }
}
