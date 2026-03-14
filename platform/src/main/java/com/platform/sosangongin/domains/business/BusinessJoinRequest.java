package com.platform.sosangongin.domains.business;

import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "business_join_requests")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessJoinRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BusinessJoinRequestStatus status = BusinessJoinRequestStatus.PENDING;

    public void approve() {
        this.status = BusinessJoinRequestStatus.APPROVED;
    }

    public void reject() {
        this.status = BusinessJoinRequestStatus.REJECTED;
    }
}
