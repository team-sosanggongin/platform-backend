package com.platform.sosangongin.cases.bankaccount;

import com.platform.sosangongin.domains.bankaccount.BankAccountVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class BankAccountListResult {
    private final List<BankAccountVo> accounts;
}
