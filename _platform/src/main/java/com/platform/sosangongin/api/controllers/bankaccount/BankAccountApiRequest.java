package com.platform.sosangongin.api.controllers.bankaccount;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BankAccountApiRequest {
    private String bankName;
    private String accountNumber;
    private String accountAlias;
}
