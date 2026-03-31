package com.platform.sosangongin.domains.role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleVo {
    private final Long id;
    private final String roleName;
    private final String description;
    private final boolean recommended;
    private final PlatformType platformType;
    private final boolean active;

    public static RoleVo from(Role role) {
        return RoleVo.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .recommended(role.isRecommended())
                .platformType(role.getPlatformType())
                .active(role.isActive())
                .build();
    }
}
