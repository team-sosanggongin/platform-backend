package com.platform.sosangongin.api.controllers;

import com.platform.sosangongin.api.controllers.dto.JoinBusinessApiRequest;
import com.platform.sosangongin.api.controllers.dto.JoinBusinessApiResponse;
import com.platform.sosangongin.api.controllers.dto.SearchBusinessApiResponse;
import com.platform.sosangongin.cases.registration.JoinBusinessUsecase;
import com.platform.sosangongin.cases.search.SearchBusinessRequest;
import com.platform.sosangongin.cases.search.SearchBusinessUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Business", description = "사업체 관련 API")
@RestController
@RequestMapping("/api/v1/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final SearchBusinessUsecase searchBusinessUsecase;
    private final JoinBusinessUsecase joinBusinessUsecase;

    @Operation(summary = "사업체 검색", description = "키워드로 사업체 이름을 검색합니다. 페이징을 지원합니다.")
    @GetMapping("/search")
    public SearchBusinessApiResponse searchBusinesses(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        SearchBusinessRequest request = SearchBusinessRequest.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();
                
        return new SearchBusinessApiResponse(searchBusinessUsecase.search(request));
    }

    @Operation(summary = "사업체 직원 등록 요청", description = "사용자가 특정 사업체에 직원으로 합류하기 위해 등록을 요청합니다.")
    @PostMapping("/{businessId}/join-requests")
    public JoinBusinessApiResponse requestToJoin(
            @PathVariable UUID businessId,
            @RequestBody JoinBusinessApiRequest body) {
        
        return new JoinBusinessApiResponse(joinBusinessUsecase.join(body.toUseCaseRequest(businessId)));
    }
}
