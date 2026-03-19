package com.platform.sosangongin.domains.token;

import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.agents.UserAgent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_value", nullable = false, columnDefinition = "TEXT")
    private String tokenValue;

    @JoinColumn(name = "user_agent_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private UserAgent userAgent;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public RefreshToken(User user, String tokenValue, UserAgent userAgent, LocalDateTime expiresAt) {
        this.user = user;
        this.tokenValue = tokenValue;
        this.userAgent = userAgent;
        this.expiresAt = expiresAt;
    }

    public boolean isTokenValueEquals(String requestToken) {
        return this.tokenValue.contentEquals(requestToken);
    }

    public boolean isBefore(LocalDateTime now) {
        return this.expiresAt.isBefore(now);
    }
}
