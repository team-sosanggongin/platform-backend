package com.platform.sosangongin.api.controllers.bankaccount;

import com.platform.sosangongin.api.resolver.LoginUser;
import com.platform.sosangongin.cases.bankaccount.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "BankAccount", description = "계좌 관리 API")
@RestController
@RequestMapping("/api/v1/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountUseCase bankAccountUseCase;

    @Operation(summary = "계좌 목록 조회", description = "로그인한 사용자의 등록된 계좌 목록을 조회합니다.")
    @GetMapping
    public BankAccountListApiResponse getAccounts(@LoginUser UUID userId) {
        return new BankAccountListApiResponse(bankAccountUseCase.getAccounts(userId.toString()));
    }

    @Operation(summary = "계좌 등록", description = "새로운 계좌를 등록합니다.")
    @PostMapping
    public BankAccountApiResponse createAccount(
            @LoginUser UUID userId,
            @RequestBody BankAccountApiRequest request) {
        return new BankAccountApiResponse(bankAccountUseCase.createAccount(
                userId.toString(),
                toUseCaseRequest(request)));
    }

    @Operation(summary = "계좌 수정", description = "등록된 계좌 정보를 수정합니다.")
    @PutMapping("/{bankAccountId}")
    public BankAccountApiResponse updateAccount(
            @LoginUser UUID userId,
            @PathVariable UUID bankAccountId,
            @RequestBody BankAccountApiRequest request) {
        return new BankAccountApiResponse(bankAccountUseCase.updateAccount(
                userId.toString(),
                bankAccountId.toString(),
                toUseCaseRequest(request)));
    }

    @Operation(summary = "계좌 삭제", description = "등록된 계좌를 삭제합니다.")
    @DeleteMapping("/{bankAccountId}")
    public BankAccountApiResponse deleteAccount(
            @LoginUser UUID userId,
            @PathVariable UUID bankAccountId) {
        return new BankAccountApiResponse(bankAccountUseCase.deleteAccount(
                userId.toString(), bankAccountId.toString()));
    }

    private BankAccountRequest toUseCaseRequest(BankAccountApiRequest request) {
        return BankAccountRequest.builder()
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountAlias(request.getAccountAlias())
                .build();
    }
}
