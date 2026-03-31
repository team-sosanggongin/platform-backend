package com.platform.sosangongin.domains.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "user_code", unique = true, length = 6, nullable = false, updatable = false)
    private String userCode;

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "is_phone_verified", nullable = false)
    private boolean isPhoneVerified;

    @Column(name = "phone_verified_at")
    private LocalDateTime phoneVerifiedAt;

    @Column(nullable = false, length = 50)
    private String name;

    public User(String phoneNumber, String userName) {
        this.phoneNumber = phoneNumber;
        this.name = userName;
    }

    public void verifyPhone() {
        this.isPhoneVerified = true;
        this.phoneVerifiedAt = LocalDateTime.now();
    }
}
