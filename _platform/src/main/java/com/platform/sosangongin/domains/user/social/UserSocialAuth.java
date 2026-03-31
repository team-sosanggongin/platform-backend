package com.platform.sosangongin.domains.user.social;

import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.user.SocialProvider;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_social_auths", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_social_auth_provider_user", columnNames = {"provider", "user_id"}),
        @UniqueConstraint(name = "uk_provider_provider_id", columnNames = {"provider, provider_user_id"})}
)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSocialAuth extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;
}
