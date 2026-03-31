package com.platform.sosangongin.api.controllers.role;

import com.platform.sosangongin.api.resolver.LoginUser;
import com.platform.sosangongin.cases.role.RoleUseCase;
import com.platform.sosangongin.cases.role.UpdateRoleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Role", description = "권한 관리 API")
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleUseCase roleUseCase;

    @Operation(summary = "내가 생성한 역할 목록 조회", description = "로그인한 사용자가 생성한 역할 목록을 조회합니다.")
    @GetMapping("/mine")
    public RoleListApiResponse getMyRoles(@Parameter(hidden = true) @LoginUser UUID userId) {
        return new RoleListApiResponse(roleUseCase.getMyRoles(userId.toString()));
    }

    @Operation(summary = "역할 생성", description = "새로운 역할을 생성하고 권한을 매핑합니다.")
    @PostMapping
    public RoleDetailApiResponse createRole(
            @Parameter(hidden = true) @LoginUser UUID userId,
            @RequestBody UpdateRoleApiRequest request) {
        return new RoleDetailApiResponse(roleUseCase.createRole(
                userId.toString(),
                UpdateRoleRequest.builder()
                        .roleName(request.getRoleName())
                        .description(request.getDescription())
                        .permissionIds(request.getPermissionIds())
                        .build()));
    }

    @Operation(summary = "역할 상세 조회", description = "역할의 상세 정보와 연결된 권한 목록을 조회합니다.")
    @GetMapping("/{roleId}")
    public RoleDetailApiResponse getRoleDetail(
            @Parameter(hidden = true) @LoginUser UUID userId,
            @PathVariable Long roleId) {
        return new RoleDetailApiResponse(roleUseCase.getRoleDetail(userId.toString(), roleId));
    }

    @Operation(summary = "역할 수정", description = "역할의 이름, 설명, 매핑된 권한을 수정합니다.")
    @PutMapping("/{roleId}")
    public RoleDetailApiResponse updateRole(
            @Parameter(hidden = true) @LoginUser UUID userId,
            @PathVariable Long roleId,
            @RequestBody UpdateRoleApiRequest request) {
        return new RoleDetailApiResponse(roleUseCase.updateRole(
                userId.toString(),
                roleId,
                UpdateRoleRequest.builder()
                        .roleName(request.getRoleName())
                        .description(request.getDescription())
                        .permissionIds(request.getPermissionIds())
                        .build()));
    }

    @Operation(summary = "활성 권한 목록 조회", description = "플랫폼에서 사용 가능한 활성 권한 목록을 permDomain 계층 순으로 조회합니다.")
    @GetMapping("/permissions")
    public PermissionListApiResponse getActivePermissions() {
        return new PermissionListApiResponse(roleUseCase.getActivePermissions());
    }
}
