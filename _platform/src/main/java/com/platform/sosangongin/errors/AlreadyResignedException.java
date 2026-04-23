package com.platform.sosangongin.errors;

import lombok.Getter;

@Getter
public class AlreadyResignedException extends PlatFormBusinessError{
    private final Long employmentId;

    public AlreadyResignedException(Long employmentId) {
        super("이미 퇴직 처리된 직원입니다: " + employmentId);
        this.employmentId = employmentId;
    }
}
