package com.platform.sosangongin.domains.user;

import com.platform.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_social_auths")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSocialAuth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider provider;

    @Column(name = "provider_user_id", unique = true, nullable = false)
    private String providerId;

    public UserSocialAuth(User user, SocialProvider provider, String providerId) {
        this.user = user;
        this.provider = provider;
        this.providerId = providerId;
    }
}
