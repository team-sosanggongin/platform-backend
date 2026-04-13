package com.backoffice.sosangongin.role.controller;

import com.backoffice.sosangongin.global.aop.RequiresRoot;
import com.backoffice.sosangongin.auth.session.SessionManager;
import com.backoffice.sosangongin.global.exception.InvalidCredentialsException;
import com.backoffice.sosangongin.role.dto.*;
import com.backoffice.sosangongin.role.usecase.RoleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleUseCase roleUseCase;
    private final SessionManager sessionManager;

    @PostMapping
    @RequiresRoot
    public ResponseEntity<RoleResponse> create(@RequestBody RoleRequest request) {
        UUID createdBy = sessionManager.getAccountId()
                .orElseThrow(() -> new InvalidCredentialsException("인증 정보가 없습니다."));
        return ResponseEntity.created(URI.create("/api/role")).body(roleUseCase.create(request, createdBy));
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> findAll() {
        return ResponseEntity.ok(roleUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(roleUseCase.findById(id));
    }

    @PutMapping("/{id}")
    @RequiresRoot
    public ResponseEntity<RoleResponse> update(@PathVariable Long id, @RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresRoot
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/permissions")
    @RequiresRoot
    public ResponseEntity<Void> updatePermissions(@PathVariable Long id,
                                                  @RequestBody RolePermissionRequest request) {
        roleUseCase.updatePermissions(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roleId}/account/{accountId}")
    @RequiresRoot
    public ResponseEntity<Void> assignRole(@PathVariable Long roleId,
                                           @PathVariable UUID accountId) {
        roleUseCase.assignRoleToAccount(accountId, roleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}/account/{accountId}")
    @RequiresRoot
    public ResponseEntity<Void> removeRole(@PathVariable Long roleId,
                                           @PathVariable UUID accountId) {
        roleUseCase.removeRoleFromAccount(accountId, roleId);
        return ResponseEntity.noContent().build();
    }
}
