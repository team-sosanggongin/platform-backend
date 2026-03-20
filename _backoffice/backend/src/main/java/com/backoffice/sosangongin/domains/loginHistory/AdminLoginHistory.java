package com.backoffice.sosangongin.domains.loginHistory;

import com.backoffice.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "admin_login_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminLoginHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID accountId;

    private String ipAddress;

    private String userAgent;

    @Column(nullable = false)
    private boolean isSuccess;

    @Builder
    public AdminLoginHistory(UUID accountId, String ipAddress, String userAgent, boolean isSuccess) {
        this.accountId = accountId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.isSuccess = isSuccess;
    }
}
