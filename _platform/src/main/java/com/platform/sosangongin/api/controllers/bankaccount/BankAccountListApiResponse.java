package com.platform.sosangongin.api.controllers.bankaccount;

import com.platform.sosangongin.cases.bankaccount.BankAccountListResult;
import com.platform.sosangongin.domains.bankaccount.BankAccountVo;
import lombok.Getter;

import java.util.List;

@Getter
public class BankAccountListApiResponse {
    private final List<BankAccountVo> accounts;

    public BankAccountListApiResponse(BankAccountListResult result) {
        this.accounts = result.getAccounts();
    }
}
