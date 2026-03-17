package com.platform.sosangongin.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class InvalidTokenException extends RuntimeException{
    private final UUID userId;
    private final String originalRefreshToken;
    private final InvalidTokenUsage usage;

    public InvalidTokenException(String message, UUID userId, String originalRefreshToken, InvalidTokenUsage usage) {
        super(message);
        this.userId = userId;
        this.originalRefreshToken = originalRefreshToken;
        this.usage = usage;
    }

    public InvalidTokenException(String message, String originalRefreshToken, InvalidTokenUsage usage) {
        super(message);
        this.originalRefreshToken = originalRefreshToken;
        this.usage = usage;
        this.userId = null;
    }
}
