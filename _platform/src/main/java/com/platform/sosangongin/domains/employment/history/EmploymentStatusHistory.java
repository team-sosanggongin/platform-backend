package com.platform.sosangongin.domains.employment.history;

import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.employment.Employment;
import com.platform.sosangongin.domains.employment.EmploymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employment_status_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmploymentStatusHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employment_id", nullable = false)
    private Employment employment;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private EmploymentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private EmploymentStatus newStatus;

    @Builder
    public EmploymentStatusHistory(Employment employment, EmploymentStatus previousStatus, EmploymentStatus newStatus) {
        this.employment = employment;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }
}
