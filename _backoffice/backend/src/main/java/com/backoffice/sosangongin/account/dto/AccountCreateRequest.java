package com.backoffice.sosangongin.account.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountCreateRequest {
    private String loginId;
    private String name;
    private String email;
    private String phoneNumber;
}