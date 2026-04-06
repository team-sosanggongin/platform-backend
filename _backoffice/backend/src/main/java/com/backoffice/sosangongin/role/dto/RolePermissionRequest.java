package com.backoffice.sosangongin.role.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RolePermissionRequest {
    private List<Long> permissionIds;
}