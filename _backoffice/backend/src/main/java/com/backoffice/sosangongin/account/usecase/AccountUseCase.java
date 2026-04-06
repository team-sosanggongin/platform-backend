package com.backoffice.sosangongin.account.usecase;

import com.backoffice.sosangongin.account.dto.*;
import com.backoffice.sosangongin.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AccountUseCase {

    private final AccountService accountService;

    public AccountResponse create(AccountCreateRequest request) {
        return AccountResponse.from(accountService.create(request));
    }

    public List<AccountResponse> findAll() {
        return accountService.findAll().stream()
                .map(AccountResponse::from)
                .collect(Collectors.toList());
    }

    public AccountResponse findById(UUID id) {
        return AccountResponse.from(accountService.findById(id));
    }

    public AccountResponse update(UUID id, AccountUpdateRequest request) {
        return AccountResponse.from(accountService.update(id, request));
    }

    public void delete(UUID id) {
        accountService.delete(id);
    }

    public void unlock(UUID id) {
        accountService.unlock(id);
    }

    public void changePassword(UUID requesterId, PasswordChangeRequest request) {
        accountService.changePassword(requesterId, request);
    }
}