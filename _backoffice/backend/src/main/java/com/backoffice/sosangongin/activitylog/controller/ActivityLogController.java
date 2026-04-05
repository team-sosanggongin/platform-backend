package com.backoffice.sosangongin.activitylog.controller;

import com.backoffice.sosangongin.activitylog.dto.AuditLogResponse;
import com.backoffice.sosangongin.activitylog.dto.LoginHistoryResponse;
import com.backoffice.sosangongin.activitylog.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity-log")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping("/audit")
    public ResponseEntity<List<AuditLogResponse>> findAllAuditLogs() {
        return ResponseEntity.ok(
                activityLogService.findAllAuditLogs().stream()
                        .map(AuditLogResponse::from)
                        .toList()
        );
    }

    @GetMapping("/audit/{accountId}")
    public ResponseEntity<List<AuditLogResponse>> findAuditLogsByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(
                activityLogService.findAuditLogsByAccountId(accountId).stream()
                        .map(AuditLogResponse::from)
                        .toList()
        );
    }

    @GetMapping("/login")
    public ResponseEntity<List<LoginHistoryResponse>> findAllLoginHistories() {
        return ResponseEntity.ok(
                activityLogService.findAllLoginHistories().stream()
                        .map(LoginHistoryResponse::from)
                        .toList()
        );
    }

    @GetMapping("/login/{accountId}")
    public ResponseEntity<List<LoginHistoryResponse>> findLoginHistoriesByAccount(@PathVariable UUID accountId) {
        return ResponseEntity.ok(
                activityLogService.findLoginHistoriesByAccountId(accountId).stream()
                        .map(LoginHistoryResponse::from)
                        .toList()
        );
    }
}