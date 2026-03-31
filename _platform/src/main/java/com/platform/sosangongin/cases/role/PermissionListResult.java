package com.platform.sosangongin.cases.role;

import com.platform.sosangongin.domains.role.PermissionVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PermissionListResult {
    private final List<PermissionVo> permissions;
}
