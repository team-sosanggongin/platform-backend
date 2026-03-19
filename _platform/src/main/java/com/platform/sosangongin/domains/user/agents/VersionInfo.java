package com.platform.sosangongin.domains.user.agents;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Builder
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class VersionInfo {

    @Column(name = "os_version_info")
    private String osVersion;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "app_version_info")
    private String appVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "agent_type")
    private AgentType agentType;


}