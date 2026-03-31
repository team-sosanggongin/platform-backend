package com.platform.sosangongin.api.controllers.bankaccount;

import com.platform.sosangongin.cases.bankaccount.BankAccountResult;
import com.platform.sosangongin.domains.bankaccount.BankAccountVo;
import lombok.Getter;

@Getter
public class BankAccountApiResponse {
    private final boolean success;
    private final BankAccountVo account;

    public BankAccountApiResponse(BankAccountResult result) {
        this.success = result.isSuccess();
        this.account = result.getAccount();
    }
}
