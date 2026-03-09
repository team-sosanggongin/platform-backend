package com.platform.sosangongin.domains.user;

import com.platform.sosangongin.domains.common.BaseLongIdEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "phone_verifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhoneVerification extends BaseLongIdEntity {

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false, length = 6)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    public PhoneVerification(String phoneNumber, String code, LocalDateTime expiredAt) {
        this.phoneNumber = phoneNumber;
        this.code = code;
        this.status = VerificationStatus.PENDING;
        this.expiredAt = expiredAt;
    }

    public boolean verify(String code, LocalDateTime requestTime) {
        if (this.status != VerificationStatus.PENDING) {
            return false;
        }

        if (requestTime.isAfter(this.expiredAt)) {
            this.status = VerificationStatus.EXPIRED;
            return false;
        }

        if (this.code.equals(code)) {
            this.status = VerificationStatus.VERIFIED;
            return true;
        }

        return false;
    }
}
