package com.platform.sosangongin.api.controllers.business;

import com.platform.sosangongin.domains.business.BusinessDto;
import com.platform.sosangongin.cases.search.SearchBusinessResult;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class SearchBusinessApiResponse {
    private final Page<BusinessDto> businesses;

    public SearchBusinessApiResponse(SearchBusinessResult result) {
        this.businesses = result.getBusinesses();
    }
}
