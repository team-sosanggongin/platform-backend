package com.platform.sosangongin.errors;

import lombok.Getter;

@Getter
public class AccessDeniedException extends PlatFormBusinessError {
    private final Object resourceId;
    private final EntityType entityType;
    private final String message;

    public AccessDeniedException(Object resourceId, EntityType entityType, String message) {
        this.resourceId = resourceId;
        this.entityType = entityType;
        this.message = message;
    }
}
