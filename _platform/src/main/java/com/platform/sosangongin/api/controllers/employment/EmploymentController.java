package com.platform.sosangongin.api.controllers.employment;

import com.platform.sosangongin.api.resolver.LoginUser;
import com.platform.sosangongin.cases.employment.EmployeeListSearchRequest;
import com.platform.sosangongin.cases.employment.EmploymentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Employment", description = "직원 관리 API")
@RestController
@RequestMapping("/api/v1/businesses/{businessId}/employees")
@RequiredArgsConstructor
public class EmploymentController {

    private final EmploymentUseCase employmentUseCase;

    @Operation(summary = "직원 목록 조회")
    @GetMapping
    public EmployeeListApiResponse listEmployees(
            @Parameter(hidden = true) @LoginUser UUID userId,
            @PathVariable UUID businessId,
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = employmentUseCase.listEmployees(userId, businessId,
                EmployeeListSearchRequest.builder()
                        .status(status)
                        .page(page)
                        .size(size)
                        .build());
        return EmployeeListApiResponse.builder()
                .employees(result.getEmployees())
                .build();
    }

    @Operation(summary = "직원 역할 사용 여부 확인")
    @GetMapping("/{employmentId}/role-usage")
    public RoleUsageApiResponse checkRoleUsage(
            @Parameter(hidden = true) @LoginUser UUID userId,
            @PathVariable UUID businessId,
            @PathVariable Long employmentId) {
        var result = employmentUseCase.checkRoleUsage(userId, businessId, employmentId);
        return RoleUsageApiResponse.builder()
                .hasAssignedRoles(result.isHasAssignedRoles())
                .roles(result.getRoles())
                .build();
    }

    @Operation(summary = "직원 퇴직 처리")
    @PatchMapping("/{employmentId}/resign")
    public ResignApiResponse resign(
            @Parameter(hidden = true) @LoginUser UUID userId,
            @PathVariable UUID businessId,
            @PathVariable Long employmentId) {
        var result = employmentUseCase.resign(userId, businessId, employmentId);
        return ResignApiResponse.builder()
                .employmentId(result.getEmploymentId())
                .userName(result.getUserName())
                .status(result.getStatus())
                .resignedAt(result.getResignedAt())
                .build();
    }
}