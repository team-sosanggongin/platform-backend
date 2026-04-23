package com.platform.sosangongin.errors;

import lombok.Getter;

@Getter
public class HasAssignedRoleException extends PlatFormBusinessError {
    private final Long employmentId;

    public HasAssignedRoleException(Long employmentId) {
        super("할당된 역할이 존재합니다: " + employmentId);
        this.employmentId = employmentId;
    }
}
