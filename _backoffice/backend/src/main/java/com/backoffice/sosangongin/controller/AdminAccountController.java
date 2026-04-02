package com.backoffice.sosangongin.controller;

import com.backoffice.sosangongin.cases.admin.AdminAccountUsecase;
import com.backoffice.sosangongin.controller.dto.admin.AdminAccountResponse;
import com.backoffice.sosangongin.controller.dto.admin.CreateAdminRequest;
import com.backoffice.sosangongin.controller.dto.admin.UpdateAdminRequest;
import com.backoffice.sosangongin.domains.account.BackofficeAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AdminAccountUsecase adminAccountUsecase;

    @PostMapping
    public ResponseEntity<AdminAccountResponse> createAdmin(@RequestBody CreateAdminRequest request) {
        UUID createdBy = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BackofficeAdmin admin = adminAccountUsecase.createAdmin(request.toCommand(), createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdminAccountResponse.from(admin));
    }

    @GetMapping
    public ResponseEntity<List<AdminAccountResponse>> getAllAdmins() {
        List<AdminAccountResponse> responses = adminAccountUsecase.getAllAdmins()
                .stream()
                .map(AdminAccountResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminAccountResponse> getAdmin(@PathVariable UUID id) {
        BackofficeAdmin admin = adminAccountUsecase.getAdmin(id);
        return ResponseEntity.ok(AdminAccountResponse.from(admin));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminAccountResponse> updateAdmin(@PathVariable UUID id, @RequestBody UpdateAdminRequest request) {
        BackofficeAdmin admin = adminAccountUsecase.updateAdmin(id, request.toCommand());
        return ResponseEntity.ok(AdminAccountResponse.from(admin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        adminAccountUsecase.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/unlock")
    public ResponseEntity<AdminAccountResponse> unlockAdmin(@PathVariable UUID id) {
        adminAccountUsecase.unlockAdmin(id);
        BackofficeAdmin admin = adminAccountUsecase.getAdmin(id);
        return ResponseEntity.ok(AdminAccountResponse.from(admin));
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<AdminAccountResponse> restoreAdmin(@PathVariable UUID id) {
        adminAccountUsecase.restoreAdmin(id);
        BackofficeAdmin admin = adminAccountUsecase.getAdmin(id);
        return ResponseEntity.ok(AdminAccountResponse.from(admin));
    }
}