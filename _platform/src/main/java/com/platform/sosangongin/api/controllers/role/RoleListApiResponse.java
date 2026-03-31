package com.platform.sosangongin.api.controllers.role;

import com.platform.sosangongin.cases.role.RoleListResult;
import com.platform.sosangongin.domains.role.RoleVo;
import lombok.Getter;

import java.util.List;

@Getter
public class RoleListApiResponse {
    private final List<RoleVo> roles;

    public RoleListApiResponse(RoleListResult result) {
        this.roles = result.getRoles();
    }
}
