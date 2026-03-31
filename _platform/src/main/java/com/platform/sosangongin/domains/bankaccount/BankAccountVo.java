package com.platform.sosangongin.domains.bankaccount;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class BankAccountVo {
    private final UUID id;
    private final String bankName;
    private final String accountNumber;
    private final String accountAlias;

    public static BankAccountVo from(BankAccount bankAccount) {
        return BankAccountVo.builder()
                .id(bankAccount.getId())
                .bankName(bankAccount.getBankName())
                .accountNumber(bankAccount.getAccountNumber())
                .accountAlias(bankAccount.getAccountAlias())
                .build();
    }
}
