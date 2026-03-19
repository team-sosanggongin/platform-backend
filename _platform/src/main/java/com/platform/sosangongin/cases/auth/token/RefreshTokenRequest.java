package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class RefreshTokenRequest extends CommonRequestTemplate {

    private final String refreshToken;
    private final UserAgentDto userAgentDto;

}
