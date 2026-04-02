package com.backoffice.sosangongin.controller.dto.admin;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class AdminAccountResponse {
    private final UUID id;
    private final String loginId;
    private final String name;
    private final String email;
    private final String phone;
    private final String status;
    private final boolean isLocked;
    private final boolean isPasswordExpired;
    private final LocalDateTime lockedAt;
    private final LocalDateTime createdAt;
    private final List<String> roles;

    public static AdminAccountResponse from(BackofficeAdmin admin) {
        return AdminAccountResponse.builder()
                .id(admin.getId())
                .loginId(admin.getLoginId())
                .name(admin.getName())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .status(resolveStatus(admin))
                .isLocked(admin.isLocked())
                .isPasswordExpired(admin.isPasswordExpired())
                .lockedAt(admin.getLockedAt())
                .createdAt(admin.getCreatedAt())
                .roles(List.of())
                .build();
    }

    private static String resolveStatus(BackofficeAdmin admin) {
        if (admin.isDeleted()) return "Inactive";
        if (admin.isLocked()) return "Locked";
        if (admin.isPasswordExpired()) return "Pending";
        return "Active";
    }
}