package com.platform.sosangongin.domains.consent;

import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_consents", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_consent_policy", columnNames = {"user_id", "consent_policy_id"})
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_policy_id", nullable = false)
    private ConsentPolicy consentPolicy;

    @Column(name = "is_agreed", nullable = false)
    private boolean isAgreed;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    public void agree() {
        this.isAgreed = true;
        this.agreedAt = LocalDateTime.now();
        this.revokedAt = null;
    }

    public void revoke() {
        this.isAgreed = false;
        this.revokedAt = LocalDateTime.now();
    }
}
