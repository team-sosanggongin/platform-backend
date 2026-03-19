package com.backoffice.sosangongin.cases.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AccountResponse {
    private UUID id;
    private String loginId;
}