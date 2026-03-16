package com.platform.sosangongin.domains.device;

import com.platform.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "device_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tokens", nullable = false, unique = true, length = 500)
    private String tokens;

    @Column(name = "device_type", nullable = false, length = 20)
    private String deviceType;

    @Builder
    public DeviceToken(Long userId, String tokens, String deviceType) {
        this.userId = userId;
        this.tokens = tokens;
        this.deviceType = deviceType;
    }

    public void updateTokens(String newToken) {
        this.tokens = newToken;
    }

    public void updateUserId(Long userId) {
        this.userId = userId;
    }
}
