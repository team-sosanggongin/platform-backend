package com.platform.sosangongin.cases.auth.login;

import com.platform.sosangongin.domains.common.ClientPlatform;
import com.platform.sosangongin.domains.user.SocialProvider;
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
    private final ClientPlatform agentType;
    private final String deviceInfo;
}
