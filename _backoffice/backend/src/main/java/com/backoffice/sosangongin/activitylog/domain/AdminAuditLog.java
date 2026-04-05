package com.backoffice.sosangongin.activitylog.domain;

import com.backoffice.sosangongin.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "admin_audit_log")
public class AdminAuditLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_domain")
    private ResourceDomain resourceDomain;

    @Column(name = "resource_id")
    private String resourceId;
}
