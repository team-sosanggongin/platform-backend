package com.platform.sosangongin.domains.user;

import com.platform.sosangongin.domains.common.BaseLongIdEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseLongIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_value", nullable = false, columnDefinition = "TEXT")
    private String tokenValue;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public RefreshToken(User user, String tokenValue, String userAgent, LocalDateTime expiresAt) {
        this.user = user;
        this.tokenValue = tokenValue;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
    }
}
