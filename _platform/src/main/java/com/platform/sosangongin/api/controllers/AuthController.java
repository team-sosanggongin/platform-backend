package com.platform.sosangongin.api.controllers;

import com.platform.sosangongin.api.controllers.dto.*;
import com.platform.sosangongin.cases.auth.login.LoginUsecase;
import com.platform.sosangongin.cases.auth.social.SocialAuthRedirectCase;
import com.platform.sosangongin.cases.auth.token.RefreshTokenUsecase;
import com.platform.sosangongin.cases.auth.verification.PhoneVerificationUsecase;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
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
    private final SocialAuthRedirectCase socialAuthRedirectCase;
    private final RefreshTokenUsecase refreshTokenUsecase;
    private final PhoneVerificationUsecase phoneVerificationUsecase;

    @Operation(summary = "소셜 로그인 리다이렉트 URL 요청", description = "제공자(KAKAO, NAVER 등)에 따른 로그인 페이지 URL을 반환합니다.")
    @GetMapping("/social/redirect")
    public SocialAuthApiResponse getSocialRedirectUrl(SocialAuthApiRequest request) {
        return new SocialAuthApiResponse(socialAuthRedirectCase.getRedirectionUrl(request.toUseCaseRequest()));
    }

    @Operation(summary = "소셜 로그인 콜백 처리", description = "소셜 로그인 후 받은 코드로 로그인을 수행합니다.")
    @PostMapping("/login")
    public LoginApiResponse login(@RequestBody LoginApiRequest request) {
        return new LoginApiResponse(loginUsecase.loginAfterSocialEvent(request.toUseCaseRequest(new UserAgentDto())));
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    @PostMapping("/refresh")
    public RefreshTokenApiResponse refresh(@RequestBody RefreshTokenApiRequest request) {
        return new RefreshTokenApiResponse(refreshTokenUsecase.reissue(request.toUseCaseRequest(new UserAgentDto())));
    }

    @Operation(summary = "휴대전화 인증 요청", description = "사용자의 휴대전화 인증을 요청하거나, 인증 코드를 검증합니다.")
    @PostMapping("/verify-phone")
    public PhoneVerificationApiResponse verifyPhone(@RequestBody PhoneVerificationApiRequest request) {
        return new PhoneVerificationApiResponse(phoneVerificationUsecase.handlePhoneVerification(request.toUseCaseRequest()));
    }
}
