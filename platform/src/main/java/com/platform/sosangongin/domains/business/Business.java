package com.platform.sosangongin.domains.business;

import com.platform.sosangongin.domains.common.SoftDeletedBaseEntity;
import com.platform.sosangongin.domains.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

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

    public Business(User owner, String bizName, BusinessType bizType, BusinessStatus status) {
        this.owner = owner;
        this.bizName = bizName;
        this.bizType = bizType;
        this.status = status;
    }
}
