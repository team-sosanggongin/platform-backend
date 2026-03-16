package com.platform.sosangongin.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class InvalidTokenException extends RuntimeException{
    private final UUID userId;
    private final String originalRefreshToken;

    public InvalidTokenException(String message, UUID userId, String originalRefreshToken) {
        super(message);
        this.userId = userId;
        this.originalRefreshToken = originalRefreshToken;
    }

    public InvalidTokenException(String message, String originalRefreshToken) {
        super(message);
        this.originalRefreshToken = originalRefreshToken;
        this.userId = null;
    }
}
