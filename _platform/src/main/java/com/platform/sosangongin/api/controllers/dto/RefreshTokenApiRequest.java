package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.auth.token.RefreshTokenRequest;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenApiRequest {
    private String refreshToken;

    public RefreshTokenRequest toUseCaseRequest(UserAgentDto userAgentDto) {
        return new RefreshTokenRequest(this.refreshToken, userAgentDto);
    }
}
