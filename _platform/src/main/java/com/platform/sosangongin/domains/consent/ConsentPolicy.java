package com.platform.sosangongin.domains.consent;

import com.platform.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "consent_policies")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsentPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title; // 예: "개인정보처리방침", "서비스 이용약관", "마케팅 수신 동의"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 약관 본문 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentType consentType;

    @Column(name = "is_required", nullable = false)
    private boolean isRequired; // 필수 동의 여부

    @Column(nullable = false, length = 20)
    private String version; // 약관 버전 (예: "1.0", "2.0")

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
