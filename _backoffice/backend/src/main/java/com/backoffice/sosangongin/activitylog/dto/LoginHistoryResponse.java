package com.backoffice.sosangongin.activitylog.dto;

import com.backoffice.sosangongin.activitylog.domain.AdminLoginHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class LoginHistoryResponse {
    private Long id;
    private UUID accountId;
    private String loginId;
    private String ipAddress;
    private String userAgent;
    private boolean isSuccess;
    private LocalDateTime createdAt;

    public static LoginHistoryResponse from(AdminLoginHistory history) {
        return LoginHistoryResponse.builder()
                .id(history.getId())
                .accountId(history.getAccountId())
                .loginId(history.getLoginId())
                .ipAddress(history.getIpAddress())
                .userAgent(history.getUserAgent())
                .isSuccess(history.isSuccess())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
