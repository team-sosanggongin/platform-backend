package com.platform.sosangongin.domains.business;

import com.platform.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "business_metadata")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessMetadata extends BaseEntity {

    @Id
    @Column(name = "business_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "business_id")
    private Business business;

    @Column(name = "pay_day")
    private Integer payDay;

    @Column(name = "start_of_week")
    private String startOfWeek;

    public BusinessMetadata(Business business, Integer payDay, String startOfWeek) {
        this.business = business;
        this.payDay = payDay;
        this.startOfWeek = startOfWeek;
    }
}
