package com.platform.sosangongin.domains.role;

import com.platform.sosangongin.domains.role.permission.Permission;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionVo {
    private final Long id;
    private final String permissionName;
    private final String permDomain;
    private final String description;
    private final boolean active;

    public static PermissionVo from(Permission permission) {
        return PermissionVo.builder()
                .id(permission.getId())
                .permissionName(permission.getPermissionName())
                .permDomain(permission.getPermDomain().getValue())
                .description(permission.getDescription())
                .active(permission.isActive())
                .build();
    }
}
