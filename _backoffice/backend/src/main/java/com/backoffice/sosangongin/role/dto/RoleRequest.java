package com.backoffice.sosangongin.role.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoleRequest {
    private String roleName;
    private String description;
}