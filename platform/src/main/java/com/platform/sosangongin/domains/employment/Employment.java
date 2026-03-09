package com.platform.sosangongin.domains.employment;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
