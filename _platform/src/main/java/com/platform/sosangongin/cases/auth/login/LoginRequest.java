package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.domains.user.SocialProvider;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class LoginRequest {
    private final String code;
    private final SocialProvider provider;
    private final UserAgentDto userAgentDto;
}
