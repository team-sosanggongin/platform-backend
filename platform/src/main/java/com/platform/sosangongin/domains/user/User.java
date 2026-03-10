package com.platform.sosangongin.domains.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_phone_number", columnList = "phone_number")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone_number", unique = true, nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "is_phone_verified", nullable = false)
    private boolean isPhoneVerified;

    @Column(name = "phone_verified_at")
    private LocalDateTime phoneVerifiedAt;

    @Column(nullable = false, length = 50)
    private String name;

    protected User(String phoneNumber, String userName) {
        this.phoneNumber = phoneNumber;
        this.name = userName;
    }

    public void verifyPhone() {
        this.isPhoneVerified = true;
        this.phoneVerifiedAt = LocalDateTime.now();
    }
}
