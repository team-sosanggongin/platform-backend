package com.platform.sosangongin.api.controllers.auth.login;

import com.platform.sosangongin.cases.auth.login.LoginUsecase;
import com.platform.sosangongin.cases.auth.verification.PhoneVerificationUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 및 인가 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUsecase loginUsecase;
    private final PhoneVerificationUsecase phoneVerificationUsecase;

    @Operation(summary = "소셜 로그인 콜백 처리", description = "OAuth 제공자의 동의 후 리다이렉트되어 code와 state를 전달받습니다.")
    @PostMapping("/login-callback")
    public LoginApiResponse login(@RequestBody SocialLoginRequest req) {
        LoginApiRequest request = new LoginApiRequest(req.getCode(), req.getProvider(), req.getAgentType(), req.getDeviceInfo());
        return new LoginApiResponse(loginUsecase.loginAfterSocialEvent(request.toUseCaseRequest()));
    }

    @Operation(summary = "휴대전화 인증 요청", description = "사용자의 휴대전화 인증을 요청하거나, 인증 코드를 검증합니다.")
    @PostMapping("/verify-phone")
    public PhoneVerificationApiResponse verifyPhone(@RequestBody PhoneVerificationApiRequest request) {
        return new PhoneVerificationApiResponse(phoneVerificationUsecase.handlePhoneVerification(request.toUseCaseRequest()));
    }
}
