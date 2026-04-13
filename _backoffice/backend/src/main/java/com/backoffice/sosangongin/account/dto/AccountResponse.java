package com.backoffice.sosangongin.account.dto;

import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import com.backoffice.sosangongin.role.dto.RoleSummary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class AccountResponse {
    private UUID id;
    private String loginId;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean isRoot;
    private boolean isLocked;
    private boolean isPasswordExpired;
    private LocalDateTime lockedAt;
    private LocalDateTime createdAt;
    private List<RoleSummary> roles;
    private List<String> permissionCodes;

    public static AccountResponse from(BackofficeAdmin admin) {
        return from(admin, Collections.emptyList(), Collections.emptyList());
    }

    public static AccountResponse from(BackofficeAdmin admin, List<RoleSummary> roles) {
        return from(admin, roles, Collections.emptyList());
    }

    public static AccountResponse from(BackofficeAdmin admin, List<RoleSummary> roles, List<String> permissionCodes) {
        return AccountResponse.builder()
                .id(admin.getId())
                .loginId(admin.getLoginId())
                .name(admin.getName())
                .email(admin.getEmail())
                .phoneNumber(admin.getPhoneNumber())
                .isRoot(admin.isRoot())
                .isLocked(admin.isLocked())
                .isPasswordExpired(admin.isPasswordExpired())
                .lockedAt(admin.getLockedAt())
                .createdAt(admin.getCreatedAt())
                .roles(roles)
                .permissionCodes(permissionCodes)
                .build();
    }
}
