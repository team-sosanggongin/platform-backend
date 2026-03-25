package com.platform.sosangongin.api.controllers.dto;

import com.platform.sosangongin.cases.CommonResultTemplate;
import com.platform.sosangongin.domains.business.BusinessDto;
import com.platform.sosangongin.cases.search.SearchBusinessResult;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class SearchBusinessApiResponse extends CommonResultTemplate {
    private final Page<BusinessDto> businesses;

    public SearchBusinessApiResponse(SearchBusinessResult result) {
        super(result.getHttpStatus(), result.getMessage());
        this.businesses = result.getBusinesses();
    }
}
