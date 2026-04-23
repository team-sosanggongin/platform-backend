package com.platform.sosangongin.cases.employment;

import com.platform.sosangongin.domains.employment.EmployeeRoleVo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoleUsageResult {
    private final boolean hasAssignedRoles;
    private final List<EmployeeRoleVo> roles;
}