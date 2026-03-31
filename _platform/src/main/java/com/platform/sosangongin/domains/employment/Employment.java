package com.platform.sosangongin.domains.employment;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.employment.history.EmploymentStatusChangedEvent;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "employments", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_business",columnNames = {"user_id, business_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Employment extends AbstractAggregateRoot<Employment> {

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

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Employment(User user, Business business, EmploymentStatus status) {
        this.user = user;
        this.business = business;
        this.status = status;
    }

    public void changeStatus(EmploymentStatus newStatus) {
        if (this.status == newStatus) {
            return;
        }
        registerEvent(new EmploymentStatusChangedEvent(this.id, this.status, newStatus));
        this.status = newStatus;
    }
}
