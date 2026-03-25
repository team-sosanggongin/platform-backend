package com.platform.sosangongin.domains.employment;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

// NOTE: (business_id, user_id) 활성 고용 중복 방지는 JPA UniqueConstraint 대신
//       DB partial index로 처리한다. 마이그레이션에 아래 DDL을 포함해야 한다:
//       CREATE UNIQUE INDEX uk_active_employment
//           ON employments (business_id, user_id)
//           WHERE deleted_at IS NULL;
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employment extends SoftDeletedBaseEntity {

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
    private EmploymentStatus status;

    public Employment(User user, Business business, EmploymentStatus status) {
        this.user = user;
        this.business = business;
        this.status = status;
    }
}
