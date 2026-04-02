package com.backoffice.sosangongin.auth.controller;

import com.backoffice.sosangongin.auth.dto.LoginRequest;
import com.backoffice.sosangongin.auth.dto.LoginResponse;
import com.backoffice.sosangongin.auth.session.SessionManager;
import com.backoffice.sosangongin.auth.usecase.LoginUseCase;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final SessionManager sessionManager;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,
                                               HttpSession session) {
        LoginResponse response = loginUseCase.execute(request);
        String role = response.isRoot() ? SessionManager.ROLE_ROOT : SessionManager.ROLE_BACKOFFICE_USER;
        sessionManager.setAccountId(session, response.getId());
        sessionManager.setRole(session, role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        sessionManager.invalidate(session);
        return ResponseEntity.ok().build();
    }
}