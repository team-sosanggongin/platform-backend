package com.platform.sosangongin.cases.search;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.business.BusinessDto;
import com.platform.sosangongin.domains.business.BusinessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SearchBusinessUsecase {

    private final BusinessRepository businessRepository;

    public SearchBusinessResult search(SearchBusinessRequest request) {
        if (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) {
            log.warn("Search keyword is empty");
            return SearchBusinessResult.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("Keyword must not be empty")
                    .build();
        }

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        
        Page<Business> businessPage = businessRepository.findByBizNameContainingIgnoreCase(
                request.getKeyword().trim(), 
                pageable
        );

        Page<BusinessDto> businessDtoPage = businessPage.map(BusinessDto::from);

        return SearchBusinessResult.builder()
                .httpStatus(HttpStatus.OK)
                .businesses(businessDtoPage)
                .build();
    }
}
