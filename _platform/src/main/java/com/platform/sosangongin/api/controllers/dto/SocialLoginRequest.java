package com.platform.sosangongin.api.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginRequest {
    private final String code;
    private final String provider;
}
