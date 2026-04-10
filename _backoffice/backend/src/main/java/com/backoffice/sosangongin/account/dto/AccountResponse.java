package com.backoffice.sosangongin.account.dto;

import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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

    public static AccountResponse from(BackofficeAdmin admin) {
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
                .build();
    }
}