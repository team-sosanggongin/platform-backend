package com.platform.sosangongin.domains.user.verification;

import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "phone_verifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneVerification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, length = 6)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private PhoneVerificationStatus status;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    public PhoneVerification(String code, LocalDateTime expiredAt) {
        this.code = code;
        this.status = PhoneVerificationStatus.PENDING;
        this.expiredAt = expiredAt;
    }

    public boolean verify(String code) {
        if (this.status != PhoneVerificationStatus.PENDING) {
            return false;
        }

        if (this.code.equals(code)) {
            this.status = PhoneVerificationStatus.VERIFIED;
            return true;
        }

        return false;
    }

    public boolean isExpired(LocalDateTime now) {
        if(this.expiredAt.isBefore(now)) {
            this.status = PhoneVerificationStatus.EXPIRED;
            return true;
        }
        return false;
    }

    public void verified() {
        this.verifiedAt = LocalDateTime.now();
        this.status = PhoneVerificationStatus.VERIFIED;
    }

    public boolean isVerifiable() {
        return this.status.equals(PhoneVerificationStatus.PENDING);
    }
}
