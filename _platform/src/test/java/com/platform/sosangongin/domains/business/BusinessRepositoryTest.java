package com.platform.sosangongin.domains.business;

import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BusinessRepositoryTest {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("가게 이름의 일부로 검색 시, 대소문자 구분 없이 페이징되어 반환되어야 한다")
    void findByBizNameContainingIgnoreCase_ReturnsPagedResults() {
        // given
        User owner = User.builder()
                .phoneNumber("01012345678")
                .name("사장님")
                .build();
        userRepository.save(owner);

        Business business1 = Business.builder()
                .owner(owner)
                .bizType(BusinessType.REGISTERED)
                .status(BusinessStatus.ACTIVE)
                .bizName("메가커피 강남점")
                .build();

        Business business2 = Business.builder()
                .owner(owner)
                .bizType(BusinessType.REGISTERED)
                .status(BusinessStatus.ACTIVE)
                .bizName("메가커피 홍대점")
                .build();

        Business business3 = Business.builder()
                .owner(owner)
                .bizType(BusinessType.REGISTERED)
                .status(BusinessStatus.ACTIVE)
                .bizName("스타벅스 강남점")
                .build();

        businessRepository.save(business1);
        businessRepository.save(business2);
        businessRepository.save(business3);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Business> resultMega = businessRepository.findByBizNameContainingIgnoreCase("메가", pageable);
        Page<Business> resultGangnam = businessRepository.findByBizNameContainingIgnoreCase("강남", pageable);
        Page<Business> resultEnglish = businessRepository.findByBizNameContainingIgnoreCase("STARBUCKS", pageable); // Assuming no match

        // then
        assertThat(resultMega.getContent()).hasSize(2);
        assertThat(resultMega.getContent()).extracting(Business::getBizName)
                .containsExactlyInAnyOrder("메가커피 강남점", "메가커피 홍대점");

        assertThat(resultGangnam.getContent()).hasSize(2);
        assertThat(resultGangnam.getContent()).extracting(Business::getBizName)
                .containsExactlyInAnyOrder("메가커피 강남점", "스타벅스 강남점");

        assertThat(resultEnglish.getContent()).isEmpty();
    }
}
