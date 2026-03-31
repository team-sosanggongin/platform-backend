package com.platform.sosangongin.api.controllers.role;

import com.platform.sosangongin.cases.role.PermissionListResult;
import com.platform.sosangongin.domains.role.PermissionVo;
import lombok.Getter;

import java.util.List;

@Getter
public class PermissionListApiResponse {
    private final List<PermissionVo> permissions;

    public PermissionListApiResponse(PermissionListResult result) {
        this.permissions = result.getPermissions();
    }
}
