package com.platform.sosangongin.api.controllers.app;

import com.platform.sosangongin.cases.app.status.CheckAppStatusUsecase;
import com.platform.sosangongin.cases.app.version.CheckAppVersionUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "App", description = "앱 상태 및 버전 체크 API")
@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class AppVersionController {

    private final CheckAppVersionUsecase checkAppVersionUsecase;
    private final CheckAppStatusUsecase checkAppStatusUsecase;

    @Operation(summary = "앱 버전 호환성 체크", description = "클라이언트 플랫폼과 앱 버전을 전달받아 호환 여부를 반환합니다. " +
            "만약, 호환이 가능하더라도, forceUpdateRequired가 true라면 반드시 유저에게 업데이트를 유도해야 합니다.")
    @PostMapping("/version-check")
    public CheckAppVersionApiResponse checkVersion(@RequestBody CheckAppVersionApiRequest request) {
        return new CheckAppVersionApiResponse(checkAppVersionUsecase.check(request.toUseCaseRequest()));
    }

    @Operation(summary = "앱 점검 상태 확인", description = "현재 앱이 점검 중인지 확인합니다. 점검 중이라면 사유와 점검 시간 정보를 반환합니다.")
    @GetMapping("/status")
    public CheckAppStatusApiResponse checkStatus() {
        return new CheckAppStatusApiResponse(checkAppStatusUsecase.check());
    }
}
