package com.backoffice.sosangongin.auth.controller;

import com.backoffice.sosangongin.auth.dto.LoginRequest;
import com.backoffice.sosangongin.auth.dto.LoginResponse;
import com.backoffice.sosangongin.auth.session.SessionManager;
import com.backoffice.sosangongin.auth.usecase.LoginUseCase;
import jakarta.servlet.http.HttpServletRequest;
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
                                               HttpServletRequest httpServletRequest) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        LoginResponse response = loginUseCase.execute(request, ipAddress, userAgent);
        String role = response.isRoot() ? SessionManager.ROLE_ROOT : SessionManager.ROLE_BACKOFFICE_USER;
        sessionManager.afterLogin(response.getId(), role, response.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        sessionManager.invalidate();
        return ResponseEntity.ok().build();
    }
}