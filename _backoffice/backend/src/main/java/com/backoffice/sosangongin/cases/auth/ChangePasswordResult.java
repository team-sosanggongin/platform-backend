package com.backoffice.sosangongin.cases.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChangePasswordResult {

    public enum Status {
        SUCCESS,
        ACCOUNT_NOT_FOUND,
        CURRENT_PASSWORD_MISMATCH,
        SAME_AS_CURRENT
    }

    private final Status status;
    private final String message;

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}