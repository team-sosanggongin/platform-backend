package com.platform.sosangongin.cases.role;

import com.platform.sosangongin.domains.role.*;
import com.platform.sosangongin.domains.role.permission.Permission;
import com.platform.sosangongin.domains.role.permission.PermissionRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.errors.AccessDeniedException;
import com.platform.sosangongin.errors.EntityNotFoundException;
import com.platform.sosangongin.errors.EntityType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class RoleUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public RoleListResult getMyRoles(String userId) throws EntityNotFoundException {
        User user = findUser(userId);

        List<RoleVo> roles = roleRepository.findByCreatedByAndPlatformType(user, PlatformType.PLATFORM).stream()
                .map(RoleVo::from)
                .toList();

        return RoleListResult.builder()
                .roles(roles)
                .build();
    }

    @Transactional(readOnly = true)
    public RoleDetailResult getRoleDetail(String userId, Long roleId) throws EntityNotFoundException {
        User user = findUser(userId);
        Role role = findRoleWithAccessCheck(roleId, user);

        List<PermissionVo> permissions = rolePermissionRepository.findByRole(role).stream()
                .map(rp -> PermissionVo.from(rp.getPermission()))
                .toList();

        return RoleDetailResult.builder()
                .role(RoleVo.from(role))
                .permissions(permissions)
                .build();
    }

    @Transactional(readOnly = true)
    public PermissionListResult getActivePermissions() {
        List<PermissionVo> permissions = permissionRepository
                .findByPlatformTypeAndIsActiveTrueAndDeletedAtIsNull(PlatformType.PLATFORM).stream()
                .sorted(Comparator.comparing(Permission::getPermDomain))
                .map(PermissionVo::from)
                .toList();

        return PermissionListResult.builder()
                .permissions(permissions)
                .build();
    }

    @Transactional
    public RoleDetailResult createRole(String userId, UpdateRoleRequest request) throws EntityNotFoundException {
        User user = findUser(userId);

        Role role = Role.builder()
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .platformType(PlatformType.PLATFORM)
                .isActive(true)
                .createdBy(user)
                .build();
        roleRepository.save(role);

        List<PermissionVo> permissionVos = saveRolePermissions(role, request.getPermissionIds());

        return RoleDetailResult.builder()
                .role(RoleVo.from(role))
                .permissions(permissionVos)
                .build();
    }

    @Transactional
    public RoleDetailResult updateRole(String userId, Long roleId, UpdateRoleRequest request) throws EntityNotFoundException {
        User user = findUser(userId);
        Role role = findRoleWithAccessCheck(roleId, user);

        role.update(request.getRoleName(), request.getDescription());

        rolePermissionRepository.deleteAllByRole(role);
        rolePermissionRepository.flush();

        List<PermissionVo> permissionVos = saveRolePermissions(role, request.getPermissionIds());

        return RoleDetailResult.builder()
                .role(RoleVo.from(role))
                .permissions(permissionVos)
                .build();
    }

    private List<PermissionVo> saveRolePermissions(Role role, List<Long> permissionIds) {
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        List<RolePermission> rolePermissions = permissions.stream()
                .map(permission -> RolePermission.builder()
                        .role(role)
                        .permission(permission)
                        .build())
                .toList();
        rolePermissionRepository.saveAll(rolePermissions);

        return permissions.stream()
                .map(PermissionVo::from)
                .toList();
    }

    private User findUser(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new EntityNotFoundException(userId, EntityType.USER, "user not found"));
    }

    private Role findRoleWithAccessCheck(Long roleId, User user) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException(roleId, EntityType.ROLE, "role not found"));

        if (!role.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException(roleId, EntityType.ROLE, "해당 역할에 대한 접근 권한이 없습니다");
        }

        return role;
    }
}
