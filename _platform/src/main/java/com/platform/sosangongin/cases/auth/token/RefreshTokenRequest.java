package com.platform.sosangongin.cases.auth.token;

import com.platform.sosangongin.cases.CommonRequestTemplate;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RefreshTokenRequest extends CommonRequestTemplate {

    private final String refreshToken;
    private final UserAgentDto userAgentDto;

}
