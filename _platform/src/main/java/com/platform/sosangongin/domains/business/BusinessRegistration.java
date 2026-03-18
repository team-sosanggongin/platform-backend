package com.platform.sosangongin.domains.business;

import com.platform.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "business_registrations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessRegistration extends BaseEntity {

    @Id
    @Column(name = "business_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "business_id")
    private Business business;

    @Column(name = "biz_number", unique = true, nullable = false)
    private String bizNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    public BusinessRegistration(Business business, String bizNumber, RegistrationStatus status) {
        this.business = business;
        this.bizNumber = bizNumber;
        this.status = status;
    }
}
