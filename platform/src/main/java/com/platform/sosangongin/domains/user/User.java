package com.platform.sosangongin.domains.user;

import com.platform.sosangongin.domains.common.BaseUuidEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_phone_number", columnList = "phone_number")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUuidEntity {

    @Column(name = "phone_number", unique = true, nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "is_phone_verified", nullable = false)
    private boolean isPhoneVerified = false;

    @Column(name = "phone_verified_at")
    private LocalDateTime phoneVerifiedAt;

    @Column(nullable = false, length = 50)
    private String name;

    public User(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public void verifyPhone() {
        this.isPhoneVerified = true;
        this.phoneVerifiedAt = LocalDateTime.now();
    }
}
