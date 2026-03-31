package com.platform.sosangongin.api.controllers.role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateRoleApiRequest {
    private String roleName;
    private String description;
    private List<Long> permissionIds;
}
