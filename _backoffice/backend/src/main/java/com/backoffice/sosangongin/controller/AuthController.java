package com.backoffice.sosangongin.controller;

import com.backoffice.sosangongin.cases.auth.LoginRequest;
import com.backoffice.sosangongin.cases.auth.LoginUsecase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginUsecase loginUsecase;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest, HttpSession session){
        loginUsecase.login(request.getLoginId(), request.getPassword(), httpServletRequest, session);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session){
        session.invalidate();   // 세션 파기
        return ResponseEntity.ok().build();
    }
}
