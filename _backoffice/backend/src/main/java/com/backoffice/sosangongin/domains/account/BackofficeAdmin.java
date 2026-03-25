package com.backoffice.sosangongin.domains.account;

import com.backoffice.sosangongin.domains.common.SoftDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "backoffice_admin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BackofficeAdmin extends SoftDeletedBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "created_by")
    private UUID createdBy;

    @Builder.Default
    @Column(name = "is_password_expired", nullable = false)
    private boolean isPasswordExpired = true;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Builder.Default
    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Builder.Default
    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    public boolean isRoot() {
        return this.createdBy == null;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
        this.isPasswordExpired = false;
        this.passwordChangedAt = LocalDateTime.now();
    }

    public void lockAccount() {
        this.isLocked = true;
        this.lockedAt = LocalDateTime.now();
    }

    public void unlockAccount() {
        this.isLocked = false;
        this.lockedAt = null;
        this.failedLoginAttempts = 0;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }
}