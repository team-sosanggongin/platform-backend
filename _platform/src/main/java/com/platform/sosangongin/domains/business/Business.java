package com.platform.sosangongin.domains.business;

import com.platform.sosangongin.domains.business.location.BusinessMetadata;
import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "businesses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Business extends SoftDeletedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "biz_name", nullable = false)
    private String bizName;

    @Enumerated(EnumType.STRING)
    @Column(name = "biz_type", nullable = false)
    private BusinessType bizType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BusinessStatus status;

    @OneToOne(mappedBy = "business", cascade = CascadeType.ALL)
    private BusinessMetadata metadata;

    // 연관관계 편의 메서드
    public void setMetadata(BusinessMetadata metadata) {
        this.metadata = metadata;
        metadata.setBusiness(this);
    }
}
