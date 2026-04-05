package com.backoffice.sosangongin.auth.usecase;

import com.backoffice.sosangongin.auth.domain.BackofficeAdmin;
import com.backoffice.sosangongin.auth.dto.LoginRequest;
import com.backoffice.sosangongin.auth.dto.LoginResponse;
import com.backoffice.sosangongin.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginUseCase {

    private final AuthService authService;

    public LoginResponse execute(LoginRequest request, String ipAddress, String userAgent) {
        BackofficeAdmin admin = authService.authenticate(request.getLoginId(), request.getPassword(), ipAddress, userAgent);

        return LoginResponse.builder()
                .id(admin.getId())
                .name(admin.getName())
                .isRoot(admin.isRoot())
                .isPasswordExpired(admin.isPasswordExpired())
                .build();
    }
}