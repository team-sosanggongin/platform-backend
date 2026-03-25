package com.platform.sosangongin.api.controllers;

import com.platform.sosangongin.api.controllers.dto.CheckAppStatusApiRequest;
import com.platform.sosangongin.api.controllers.dto.CheckAppStatusApiResponse;
import com.platform.sosangongin.cases.app.CheckAppStatusUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "App", description = "앱 상태 관련 API")
@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class AppController {

    private final CheckAppStatusUsecase checkAppStatusUsecase;

    @Operation(summary = "앱 상태 확인", description = "서비스 점검 여부 및 클라이언트 앱 버전 호환성을 확인합니다.")
    @GetMapping("/status")
    public CheckAppStatusApiResponse checkStatus(CheckAppStatusApiRequest request) {
        return new CheckAppStatusApiResponse(checkAppStatusUsecase.check(request.toUseCaseRequest()));
    }
}
