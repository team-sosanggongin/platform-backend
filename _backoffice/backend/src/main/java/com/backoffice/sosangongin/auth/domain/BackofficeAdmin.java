package com.backoffice.sosangongin.auth.domain;

import com.backoffice.sosangongin.global.entity.SoftDeletedBaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "backoffice_admin")
public class BackofficeAdmin extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "login_id", unique = true, nullable = false)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "is_root", nullable = false)
    private boolean isRoot = false;

    @Column(name = "is_password_expired", nullable = false)
    @Builder.Default
    private boolean isPasswordExpired = true;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(name = "failed_login_attempts", nullable = false)
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private boolean isLocked = false;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    public void increaseFailedAttempts() {
        this.failedLoginAttempts++;
    }

    public void lock() {
        this.isLocked = true;
        this.lockedAt = LocalDateTime.now();
    }

    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
    }

    public void update(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void unlock() {
        this.isLocked = false;
        this.lockedAt = null;
        this.failedLoginAttempts = 0;
    }

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
        this.isPasswordExpired = false;
        this.passwordChangedAt = LocalDateTime.now();
    }

}