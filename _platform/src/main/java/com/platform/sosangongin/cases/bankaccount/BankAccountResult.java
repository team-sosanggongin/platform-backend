package com.platform.sosangongin.cases.bankaccount;

import com.platform.sosangongin.domains.bankaccount.BankAccountVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BankAccountResult {
    private final boolean success;
    private final BankAccountVo account;
}
