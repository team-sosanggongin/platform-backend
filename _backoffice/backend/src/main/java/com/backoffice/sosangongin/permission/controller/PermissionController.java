package com.backoffice.sosangongin.permission.controller;

import com.backoffice.sosangongin.activitylog.domain.ActionType;
import com.backoffice.sosangongin.activitylog.domain.ResourceDomain;
import com.backoffice.sosangongin.global.aop.AuditLog;
import com.backoffice.sosangongin.global.aop.RequiresRoot;
import com.backoffice.sosangongin.permission.dto.*;
import com.backoffice.sosangongin.permission.usecase.PermissionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionUseCase permissionUseCase;

    @PostMapping
    @RequiresRoot
    @AuditLog(action = ActionType.CREATE, domain = ResourceDomain.PERMISSION)
    public ResponseEntity<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ResponseEntity.created(URI.create("/api/permission")).body(permissionUseCase.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> findAll() {
        return ResponseEntity.ok(permissionUseCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionUseCase.findById(id));
    }

    @PutMapping("/{id}")
    @RequiresRoot
    @AuditLog(action = ActionType.UPDATE, domain = ResourceDomain.PERMISSION, resourceIdParam = "id")
    public ResponseEntity<PermissionResponse> update(@PathVariable Long id,
                                                     @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(permissionUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresRoot
    @AuditLog(action = ActionType.DELETE, domain = ResourceDomain.PERMISSION, resourceIdParam = "id")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
