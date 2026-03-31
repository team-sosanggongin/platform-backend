package com.platform.sosangongin.api.controllers.consent;

import com.platform.sosangongin.api.resolver.LoginUser;
import com.platform.sosangongin.cases.consent.AgreeConsentRequest;
import com.platform.sosangongin.cases.consent.ConsentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Consent", description = "약관 동의 API")
@RestController
@RequestMapping("/api/v1/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentUseCase consentUseCase;

    @Operation(summary = "미동의 약관 목록 조회", description = "로그인한 사용자가 아직 동의하지 않은 활성 약관 목록을 조회합니다.")
    @GetMapping("/pending")
    public PendingConsentApiResponse getPendingConsents(@LoginUser UUID userId) {
        return new PendingConsentApiResponse(consentUseCase.getPendingConsents(userId.toString()));
    }

    @Operation(summary = "약관 동의/철회", description = "특정 약관에 대해 동의 또는 철회합니다.")
    @PostMapping("/agree")
    public AgreeConsentApiResponse agreeConsent(
            @LoginUser UUID userId,
            @RequestBody AgreeConsentApiRequest request) {
        return new AgreeConsentApiResponse(consentUseCase.agreeConsent(
                AgreeConsentRequest.builder()
                        .userId(userId.toString())
                        .consentPolicyId(request.getConsentPolicyId())
                        .agreed(request.isAgreed())
                        .build()));
    }
}
