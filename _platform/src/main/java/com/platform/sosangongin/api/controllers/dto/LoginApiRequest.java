package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.auth.login.LoginRequest;
import com.platform.sosangongin.domains.user.SocialProvider;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Provider;
import java.util.Base64;

@Getter
@AllArgsConstructor
public class LoginApiRequest {
    private String code;
    private String provider;

    public LoginRequest toUseCaseRequest(UserAgentDto userAgentDto) {
        return new LoginRequest(code, SocialProvider.valueOf(provider.toUpperCase()), userAgentDto);
    }
}
