package com.platform.sosangongin.domains.role;

import java.util.Set;

public class PredefinedPermissionFactory {
    public Set<RolePermission> permissions(){
        return Set.of(
                new RolePermission(1L, "owner", new PermissionDomain("*"))
        );
    }
}
