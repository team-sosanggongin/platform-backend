package com.backoffice.sosangongin.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LoginResponse {
    private UUID id;
    private String name;
    private boolean isRoot;
    private boolean isPasswordExpired;
}