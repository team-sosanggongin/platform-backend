package com.platform.sosangongin.cases.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UpdateRoleRequest {
    private final String roleName;
    private final String description;
    private final List<Long> permissionIds;
}
