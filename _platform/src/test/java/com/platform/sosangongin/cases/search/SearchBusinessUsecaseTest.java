package com.platform.sosangongin.cases.search;

import com.platform.sosangongin.domains.business.*;
import com.platform.sosangongin.domains.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SearchBusinessUsecaseTest {

    @Mock
    private BusinessRepository businessRepository;

    @InjectMocks
    private SearchBusinessUsecase searchBusinessUsecase;

    @Test
    @DisplayName("가게 이름 검색 성공 - 결과가 있는 경우 (메타데이터 포함)")
    void search_Success_WithResults() {
        // given
        String keyword = "커피";
        int page = 0;
        int size = 10;
        SearchBusinessRequest request = SearchBusinessRequest.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        User owner = User.builder().build();
        Business business = Business.builder()
                .owner(owner)
                .bizType(BusinessType.REGISTERED)
                .status(BusinessStatus.ACTIVE)
                .bizName("메가커피 강남점")
                .build();
        ReflectionTestUtils.setField(business, "id", UUID.randomUUID());

        BusinessMetadata metadata = new BusinessMetadata(business);
        business.setMetadata(metadata);

        Page<Business> businessPage = new PageImpl<>(List.of(business), PageRequest.of(page, size), 1);

        given(businessRepository.findByBizNameContainingIgnoreCase(eq(keyword), any(Pageable.class)))
                .willReturn(businessPage);

        // when
        SearchBusinessResult result = searchBusinessUsecase.search(request);

        // then
        assertThat(result.getBusinesses()).isNotNull();
        assertThat(result.getBusinesses().getTotalElements()).isEqualTo(1);
        
        BusinessDto dto = result.getBusinesses().getContent().get(0);
        assertThat(dto.getBizName()).isEqualTo("메가커피 강남점");

        verify(businessRepository).findByBizNameContainingIgnoreCase(eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("가게 이름 검색 성공 - 검색 결과가 없는 경우")
    void search_Success_NoResults() {
        // given
        String keyword = "없는가게";
        int page = 0;
        int size = 10;
        SearchBusinessRequest request = SearchBusinessRequest.builder()
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        Page<Business> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);

        given(businessRepository.findByBizNameContainingIgnoreCase(eq(keyword), any(Pageable.class)))
                .willReturn(emptyPage);

        // when
        SearchBusinessResult result = searchBusinessUsecase.search(request);

        // then
        assertThat(result.getBusinesses()).isNotNull();
        assertThat(result.getBusinesses().isEmpty()).isTrue();
        assertThat(result.getBusinesses().getTotalElements()).isEqualTo(0);
        verify(businessRepository).findByBizNameContainingIgnoreCase(eq(keyword), any(Pageable.class));
    }

    @Test
    @DisplayName("가게 이름 검색 실패 - 키워드가 null인 경우")
    void search_Fail_NullKeyword() {
        // given
        SearchBusinessRequest request = SearchBusinessRequest.builder()
                .keyword(null)
                .build();

        // when
        SearchBusinessResult result = searchBusinessUsecase.search(request);

        // then
        assertThat(result.getBusinesses()).isNull();
        verifyNoInteractions(businessRepository);
    }

    @Test
    @DisplayName("가게 이름 검색 실패 - 키워드가 빈 문자열인 경우")
    void search_Fail_EmptyKeyword() {
        // given
        SearchBusinessRequest request = SearchBusinessRequest.builder()
                .keyword("   ")
                .build();

        // when
        SearchBusinessResult result = searchBusinessUsecase.search(request);

        // then
        assertThat(result.getBusinesses()).isNull();
        verifyNoInteractions(businessRepository);
    }
}
