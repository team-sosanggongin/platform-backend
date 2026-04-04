package com.backoffice.sosangongin.permission.dto;

import com.backoffice.sosangongin.permission.domain.PermissionBackoffice;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PermissionResponse {
    private Long id;
    private String permissionName;
    private String permDomain;

    public static PermissionResponse from(PermissionBackoffice permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .permissionName(permission.getPermissionName())
                .permDomain(permission.getPermDomain())
                .build();
    }
}