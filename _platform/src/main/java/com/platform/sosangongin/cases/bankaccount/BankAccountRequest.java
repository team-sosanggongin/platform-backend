package com.platform.sosangongin.cases.bankaccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BankAccountRequest {
    private final String bankName;
    private final String accountNumber;
    private final String accountAlias;
}
