package com.backoffice.sosangongin.permission.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PermissionRequest {
    private String permissionName;
    private String permDomain;
}