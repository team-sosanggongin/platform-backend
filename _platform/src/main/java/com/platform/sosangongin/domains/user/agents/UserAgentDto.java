package com.platform.sosangongin.domains.user.agents;

import com.platform.sosangongin.domains.common.ClientPlatform;
import com.platform.sosangongin.domains.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAgentDto {

    private String osVersion;
    private String deviceInfo;
    private String appVersion;
    private String pushToken;
    private ClientPlatform agentType;

    public UserAgent toEntity(User user) {
        return UserAgent.builder()
                .user(user)
                .osVersion(osVersion)
                .deviceInfo(deviceInfo)
                .appVersion(appVersion)
                .pushToken(pushToken)
                .agentType(agentType)
                .build();
    }
}