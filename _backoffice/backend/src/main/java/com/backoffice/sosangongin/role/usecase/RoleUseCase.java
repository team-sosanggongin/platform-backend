package com.backoffice.sosangongin.role.usecase;

import com.backoffice.sosangongin.role.dto.*;
import com.backoffice.sosangongin.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoleUseCase {

    private final RoleService roleService;

    public RoleResponse create(RoleRequest request, UUID createdBy) {
        return RoleResponse.from(roleService.create(request.getRoleName(), request.getDescription(), createdBy));
    }

    public List<RoleResponse> findAll() {
        return roleService.findAll().stream()
                .map(RoleResponse::from)
                .toList();
    }

    public RoleResponse findById(Long id) {
        return RoleResponse.from(roleService.findById(id));
    }

    public RoleResponse update(Long id, RoleRequest request) {
        return RoleResponse.from(roleService.update(id, request.getRoleName(), request.getDescription()));
    }

    public void delete(Long id) {
        roleService.delete(id);
    }

    public void updatePermissions(Long roleId, RolePermissionRequest request) {
        roleService.updatePermissions(roleId, request.getPermissionIds());
    }

    public void assignRoleToAccount(UUID accountId, Long roleId) {
        roleService.assignRoleToAccount(accountId, roleId);
    }

    public void removeRoleFromAccount(UUID accountId, Long roleId) {
        roleService.removeRoleFromAccount(accountId, roleId);
    }
}