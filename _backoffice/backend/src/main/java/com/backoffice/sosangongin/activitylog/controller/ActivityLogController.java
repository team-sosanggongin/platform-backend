package com.backoffice.sosangongin.activitylog.controller;

import com.backoffice.sosangongin.activitylog.dto.AuditLogResponse;
import com.backoffice.sosangongin.activitylog.dto.LoginHistoryResponse;
import com.backoffice.sosangongin.activitylog.service.ActivityLogService;
import com.backoffice.sosangongin.global.aop.RequiresRoot;
import com.backoffice.sosangongin.global.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/activity-log")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping("/audit")
    @RequiresRoot
    public ResponseEntity<PageResponse<AuditLogResponse>> findAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                PageResponse.from(activityLogService.findAllAuditLogs(pageable)
                        .map(AuditLogResponse::from))
        );
    }

    @GetMapping("/audit/{accountId}")
    @RequiresRoot
    public ResponseEntity<PageResponse<AuditLogResponse>> findAuditLogsByAccount(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                PageResponse.from(activityLogService.findAuditLogsByAccountId(accountId, pageable)
                        .map(AuditLogResponse::from))
        );
    }

    @GetMapping("/login")
    @RequiresRoot
    public ResponseEntity<PageResponse<LoginHistoryResponse>> findAllLoginHistories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                PageResponse.from(activityLogService.findAllLoginHistories(pageable)
                        .map(LoginHistoryResponse::from))
        );
    }

    @GetMapping("/login/{accountId}")
    @RequiresRoot
    public ResponseEntity<PageResponse<LoginHistoryResponse>> findLoginHistoriesByAccount(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                PageResponse.from(activityLogService.findLoginHistoriesByAccountId(accountId, pageable)
                        .map(LoginHistoryResponse::from))
        );
    }
}
