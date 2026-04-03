package com.backoffice.sosangongin.account.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountUpdateRequest {
    private String name;
    private String email;
    private String phoneNumber;
}