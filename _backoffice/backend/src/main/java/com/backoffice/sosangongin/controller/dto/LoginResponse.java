package com.backoffice.sosangongin.controller.dto;

import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LoginResponse {
    private final UUID accountId;
    private final String name;
    private final boolean passwordExpired;

    public static LoginResponse from(BackofficeAdmin admin) {
        return LoginResponse.builder()
                .accountId(admin.getId())
                .name(admin.getName())
                .passwordExpired(admin.isPasswordExpired())
                .build();
    }
}
