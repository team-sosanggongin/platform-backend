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
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "business_id")
    private Business business;


    public BusinessMetadata(Business business) {
        this.business = business;
    }
}
