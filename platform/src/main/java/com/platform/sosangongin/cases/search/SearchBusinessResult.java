package com.platform.sosangongin.cases.search;

import com.platform.sosangongin.cases.CommonResultTemplate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Getter
public class SearchBusinessResult extends CommonResultTemplate {

    private final Page<BusinessDto> businesses;

    @Builder
    public SearchBusinessResult(HttpStatus httpStatus, String message, Page<BusinessDto> businesses) {
        super(httpStatus, message);
        this.businesses = businesses;
    }
}
