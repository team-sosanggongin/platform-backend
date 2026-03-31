package com.platform.sosangongin.cases.search;

import com.platform.sosangongin.domains.business.BusinessDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class SearchBusinessResult {

    private final Page<BusinessDto> businesses;

    @Builder
    public SearchBusinessResult(Page<BusinessDto> businesses) {
        this.businesses = businesses;
    }
}
