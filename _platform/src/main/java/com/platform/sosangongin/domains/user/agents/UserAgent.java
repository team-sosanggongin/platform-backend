package com.platform.sosangongin.domains.user.agents;

import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "user_agent")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAgent extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Embedded
    private VersionInfo versionInfo;

    @Column(name = "app_push_token", unique = true)
    private String pushToken;

    @Builder.Default
    @Column(name = "is_active") // 로그아웃하거나 앱 삭제 시 푸시 발송 대상에서 제외하기 위함
    private boolean isActive = true;

}
