package com.platform.sosangongin.domains.employment;

import com.platform.sosangongin.domains.role.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeRoleVo {
    private final Long roleId;
    private final String roleName;

    public static EmployeeRoleVo from(Role role) {
        return EmployeeRoleVo.builder()
                .roleId(role.getId())
                .roleName(role.getRoleName())
                .build();
    }
}