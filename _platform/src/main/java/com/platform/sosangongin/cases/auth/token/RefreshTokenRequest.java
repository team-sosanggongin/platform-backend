package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class RefreshTokenRequest {

    private final String refreshToken;
    private final UserAgentDto userAgentDto;

}
