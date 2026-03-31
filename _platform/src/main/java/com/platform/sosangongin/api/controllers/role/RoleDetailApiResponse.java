package com.platform.sosangongin.api.controllers.role;

import com.platform.sosangongin.cases.role.RoleDetailResult;
import com.platform.sosangongin.domains.role.PermissionVo;
import com.platform.sosangongin.domains.role.RoleVo;
import lombok.Getter;

import java.util.List;

@Getter
public class RoleDetailApiResponse {
    private final RoleVo role;
    private final List<PermissionVo> permissions;

    public RoleDetailApiResponse(RoleDetailResult result) {
        this.role = result.getRole();
        this.permissions = result.getPermissions();
    }
}
