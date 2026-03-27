package com.platform.sosangongin.domains.business.location;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "business_metadata")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessMetadata extends BaseEntity {

    @Id
    @Column(name = "business_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "business_id")
    private Business business;

    @Embedded
    private BusinessLocation location;

    public BusinessMetadata(Business business) {
        this.business = business;
    }
}
