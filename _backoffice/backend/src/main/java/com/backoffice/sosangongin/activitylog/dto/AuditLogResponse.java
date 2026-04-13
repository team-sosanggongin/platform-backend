package com.backoffice.sosangongin.activitylog.dto;

import com.backoffice.sosangongin.activitylog.domain.ActionType;
import com.backoffice.sosangongin.activitylog.domain.AdminAuditLog;
import com.backoffice.sosangongin.activitylog.domain.ResourceDomain;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class AuditLogResponse {
    private Long id;
    private UUID accountId;
    private ActionType actionType;
    private ResourceDomain resourceDomain;
    private String resourceId;
    private LocalDateTime createdAt;

    public static AuditLogResponse from(AdminAuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .accountId(log.getAccountId())
                .actionType(log.getActionType())
                .resourceDomain(log.getResourceDomain())
                .resourceId(log.getResourceId())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
