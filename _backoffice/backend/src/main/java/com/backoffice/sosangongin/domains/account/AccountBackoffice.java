package com.backoffice.sosangongin.domains.account;

import com.backoffice.sosangongin.domains.common.SoftDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_backoffice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AccountBackoffice extends SoftDeletedBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;    // _Platform 의 users 테이블 참조

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Builder.Default
    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Builder.Default
    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    /**
     * 상태 변경 메서드 (setter 대체)
     */
    public void lockAccount() {
        this.isLocked = true;
        this.lockedAt = LocalDateTime.now();
    }

    public void unlockAccount() {
        this.isLocked = false;
        this.lockedAt = null;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }
}
