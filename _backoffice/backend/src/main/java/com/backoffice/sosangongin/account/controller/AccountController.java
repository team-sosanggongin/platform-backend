package com.backoffice.sosangongin.account.controller;

import com.backoffice.sosangongin.account.dto.AccountCreateRequest;
import com.backoffice.sosangongin.account.dto.AccountResponse;
import com.backoffice.sosangongin.account.dto.AccountUpdateRequest;
import com.backoffice.sosangongin.account.dto.PasswordChangeRequest;
import com.backoffice.sosangongin.account.usecase.AccountUseCase;
import com.backoffice.sosangongin.auth.session.SessionManager;
import com.backoffice.sosangongin.global.exception.InvalidCredentialsException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountUseCase accountUseCase;
    private final SessionManager sessionManager;

    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody AccountCreateRequest request) {
        return ResponseEntity.status(201).body(accountUseCase.create(request));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(accountUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(accountUseCase.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountResponse> update(@PathVariable UUID id,
                                                  @RequestBody AccountUpdateRequest request) {
        return ResponseEntity.ok(accountUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        accountUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/unlock")
    public ResponseEntity<Void> unlock(@PathVariable UUID id) {
        accountUseCase.unlock(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeRequest request,
                                               HttpSession session) {
        UUID requesterId = sessionManager.getAccountId(session)
                .orElseThrow(() -> new InvalidCredentialsException("인증 정보가 없습니다."));
        accountUseCase.changePassword(requesterId, request);
        return ResponseEntity.ok().build();
    }
}