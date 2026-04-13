package com.backoffice.sosangongin.role.dto;

import com.backoffice.sosangongin.role.domain.RoleBackoffice;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoleSummary {
    private Long id;
    private String roleName;
    private String description;

    public static RoleSummary from(RoleBackoffice role) {
        return RoleSummary.builder()
                .id(role.getId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .build();
    }
}
