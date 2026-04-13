package com.backoffice.sosangongin.activitylog.domain;

import com.backoffice.sosangongin.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "admin_login_history")
public class AdminLoginHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "is_success", nullable = false)
    private boolean isSuccess;
}