package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.auth.login.LoginRequest;
import com.platform.sosangongin.domains.user.SocialProvider;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginApiRequest {
    private String code;
    private SocialProvider provider;

    public LoginRequest toUseCaseRequest(UserAgentDto userAgentDto) {
        return new LoginRequest(code, provider, userAgentDto);
    }
}
