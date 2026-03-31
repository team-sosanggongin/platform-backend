package com.backoffice.sosangongin.controller;

import com.backoffice.sosangongin.cases.auth.*;
import com.backoffice.sosangongin.config.SessionManager;
import com.backoffice.sosangongin.controller.dto.LoginResponse;
import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import com.backoffice.sosangongin.errors.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginUsecase loginUsecase;
    private final ChangePasswordUsecase changePasswordUsecase;
    private final GetMyInfoUsecase getMyInfoUsecase;
    private final SessionManager sessionManager;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest, HttpSession session) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        String userAgent = httpServletRequest.getHeader("User-Agent");

        BackofficeAdmin admin = loginUsecase.login(request.getLoginId(), request.getPassword(), ipAddress, userAgent);
        sessionManager.setAccountId(session, admin.getId());

        return ResponseEntity.ok(LoginResponse.from(admin));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        UUID accountId = sessionManager.getAccountId(session).orElse(null);
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.builder().message("로그인이 필요합니다.").build());
        }

        BackofficeAdmin admin = getMyInfoUsecase.getMyInfo(accountId);
        return ResponseEntity.ok(LoginResponse.from(admin));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, HttpSession session) {
        UUID accountId = sessionManager.getAccountId(session).orElse(null);
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.builder().message("로그인이 필요합니다.").build());
        }

        ChangePasswordResult result = changePasswordUsecase.changePassword(
                accountId, request.getCurrentPassword(), request.getNewPassword());

        if (result.isSuccess()) {
            return ResponseEntity.ok().build();
        }

        HttpStatus status = switch (result.getStatus()) {
            case CURRENT_PASSWORD_MISMATCH, SAME_AS_CURRENT -> HttpStatus.BAD_REQUEST;
            case ACCOUNT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(status)
                .body(ErrorResponse.builder().message(result.getMessage()).build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        sessionManager.invalidate(session);
        return ResponseEntity.ok().build();
    }
}