package com.platform.sosangongin.domains.token;

import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.common.ClientPlatform;
import com.platform.sosangongin.domains.user.User;
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

    @Column(name = "token_value", nullable = false, unique = true, columnDefinition = "TEXT")
    private String tokenValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "agent_type", nullable = false)
    private ClientPlatform agentType;

    @Column(name = "device_info", nullable = false)
    private String deviceInfo;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    public RefreshToken(String tokenValue, User user, ClientPlatform agentType, String deviceInfo, LocalDateTime now) {
        this.tokenValue = tokenValue;
        this.user = user;
        this.agentType = agentType;
        this.deviceInfo = deviceInfo;
        this.expiresAt = now.plusDays(7);
    }

    /**
     * @param now
     * @return refreshToken의 만료시간이 10시간도 남아있지 않은 경우
     */
    public boolean shouldRefreshTokenRefreshed(LocalDateTime now) {
        return this.expiresAt.minusHours(10).isBefore(now);
    }

    public boolean isTokenValueEquals(String requestToken) {
        return this.tokenValue.contentEquals(requestToken);
    }

    public boolean isBefore(LocalDateTime now) {
        return this.expiresAt.isBefore(now);
    }

    public boolean isDeviceMatch(ClientPlatform agentType, String deviceInfo) {
        return this.agentType.equals(agentType) && this.deviceInfo.equals(deviceInfo);
    }
}
