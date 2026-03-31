package com.platform.sosangongin.cases.role;

import com.platform.sosangongin.domains.role.PermissionVo;
import com.platform.sosangongin.domains.role.RoleVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RoleDetailResult {
    private final RoleVo role;
    private final List<PermissionVo> permissions;
}
