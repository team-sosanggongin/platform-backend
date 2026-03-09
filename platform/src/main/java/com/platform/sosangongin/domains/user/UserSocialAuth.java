package com.platform.sosangongin.domains.user;

import com.platform.sosangongin.domains.common.BaseLongIdEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_social_auths")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSocialAuth extends BaseLongIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider provider;

    @Column(name = "provider_user_id", unique = true, nullable = false)
    private String providerUserId;

    public UserSocialAuth(User user, SocialProvider provider, String providerUserId) {
        this.user = user;
        this.provider = provider;
        this.providerUserId = providerUserId;
    }
}
