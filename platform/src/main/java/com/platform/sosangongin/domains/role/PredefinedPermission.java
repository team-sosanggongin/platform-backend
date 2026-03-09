package com.platform.sosangongin.domains.role;

import java.util.Set;

public class PredefinedPermission {
    public Set<RolePermission> domains(){
        return Set.of(
                new RolePermission(1L, "owner", new PermissionDomain("*"))
        );
    }
}
