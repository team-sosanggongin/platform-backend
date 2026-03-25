package com.backoffice.sosangongin.controller;

import com.backoffice.sosangongin.cases.auth.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginUsecase loginUsecase;
    private final ChangePasswordUsecase changePasswordUsecase;

    @PostMapping("/login")
    public ResponseEntity<LoginResult> login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest, HttpSession session) {
        LoginResult result = loginUsecase.login(request.getLoginId(), request.getPassword(), httpServletRequest, session);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request, HttpSession session) {
        UUID accountId = (UUID) session.getAttribute("ACCOUNT_ID");
        if (accountId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        changePasswordUsecase.changePassword(accountId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}