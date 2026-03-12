package com.platform.sosangongin.services.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secretKey = "sosangongin-platform-backend-secret-key-must-be-long-enough";
    private long expirationTime = 900000; // 15 minutes
    private long refreshTokenExpirationTime = 1209600000; // 14 days
}
