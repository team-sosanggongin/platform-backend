package com.platform.sosangongin.domains.employment;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;

// TODO: soft delete된 레코드가 존재하는 상태에서 같은 (user_id, business_id) 재고용 시
//       uk_employment_user_business 제약조건 충돌 가능. hard delete 또는 unique 조건에 deletedAt 포함 필요.
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_employment_user_business", columnNames = {"business_id", "user_id"})
})
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
