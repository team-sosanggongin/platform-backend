package com.platform.sosangongin.errors;

import lombok.Getter;

/**
 * @apiNote 존재해야하나, 존재하지 않는 경우, 예를 들어, API 호출을 수행한 유저가 실제로는 존재하지 않는 경우 예외
 */
@Getter
public class EntityNotFoundException extends PlatFormBusinessError{
    private Object uniqueId;
    private EntityType entityType;
    private String message;

    public EntityNotFoundException(Object uniqueId, EntityType entityType, String message) {
        this.uniqueId = uniqueId;
        this.entityType = entityType;
        this.message = message;
    }
}

