package com.backoffice.sosangongin.cases.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResult {
    private boolean passwordExpired;
}