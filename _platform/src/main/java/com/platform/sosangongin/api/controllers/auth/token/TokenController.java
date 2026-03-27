package com.platform.sosangongin.api.controllers.auth.token;

import com.platform.sosangongin.cases.auth.token.RefreshTokenUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Token", description = "Token 관련 API")
@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
public class TokenController {

    private final RefreshTokenUsecase refreshTokenUsecase;

    @Operation(summary = "액세스 토큰 재발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다. " +
            "리프레시 토큰 만료가 임박한 경우 리프레시 토큰도 함께 재발급됩니다.")
    @PostMapping("/refresh")
    public RefreshTokenApiResponse refresh(@RequestBody RefreshTokenApiRequest request) {
        return new RefreshTokenApiResponse(refreshTokenUsecase.reissue(request.toUseCaseRequest()));
    }
}
