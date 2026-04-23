package com.platform.sosangongin.errors;

import lombok.Getter;

@Getter
public class InvalidEmploymentStatusException extends PlatFormBusinessError {
    private final String invalidStatus;

    public InvalidEmploymentStatusException(String invalidStatus) {
        super("유효하지 않은 상태값: " + invalidStatus
                + " (허용: [ACTIVE, RESIGNED, PENDING, VACATION, ALL])");
        this.invalidStatus = invalidStatus;
    }
}
