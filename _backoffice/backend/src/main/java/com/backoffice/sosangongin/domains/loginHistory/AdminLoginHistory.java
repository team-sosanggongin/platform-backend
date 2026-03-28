package com.backoffice.sosangongin.domains.loginHistory;

import com.backoffice.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "admin_login_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
}
