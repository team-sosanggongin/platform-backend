package com.backoffice.sosangongin.permission.usecase;

import com.backoffice.sosangongin.permission.dto.*;
import com.backoffice.sosangongin.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionUseCase {

    private final PermissionService permissionService;

    public PermissionResponse create(PermissionRequest request) {
        return PermissionResponse.from(permissionService.create(request.getPermissionName(), request.getPermDomain()));
    }

    public List<PermissionResponse> findAll() {
        return permissionService.findAll().stream()
                .map(PermissionResponse::from)
                .toList();
    }

    public PermissionResponse findById(Long id) {
        return PermissionResponse.from(permissionService.findById(id));
    }

    public PermissionResponse update(Long id, PermissionRequest request) {
        return PermissionResponse.from(permissionService.update(id, request.getPermissionName(), request.getPermDomain()));
    }

    public void delete(Long id) {
        permissionService.delete(id);
    }
}