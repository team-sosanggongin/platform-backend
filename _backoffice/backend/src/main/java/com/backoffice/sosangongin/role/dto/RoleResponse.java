package com.backoffice.sosangongin.role.dto;

import com.backoffice.sosangongin.permission.dto.PermissionResponse;
import com.backoffice.sosangongin.role.domain.RoleBackoffice;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoleResponse {
    private Long id;
    private String roleName;
    private String description;
    private List<PermissionResponse> permissions;

    public static RoleResponse from(RoleBackoffice role) {
        return RoleResponse.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .permissions(role.getRolePermissions().stream()
                        .map(rp -> PermissionResponse.from(rp.getPermission()))
                        .toList())
                .build();
    }
}