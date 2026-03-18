package com.platform.sosangongin.cases.registration;

import com.platform.sosangongin.domains.business.*;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.services.external.SmsPushService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class JoinBusinessUsecaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BusinessRepository businessRepository;
    @Mock
    private BusinessJoinRequestRepository businessJoinRequestRepository;
    @Mock
    private SmsPushService smsPushService;

    @InjectMocks
    private JoinBusinessUsecase joinBusinessUsecase;

    @Test
    @DisplayName("사업체 등록 요청 성공 - 모든 조건을 만족하는 경우 OK 반환 및 데이터 저장, SMS 발송")
    void join_Success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();
        JoinBusinessRequest request = JoinBusinessRequest.builder()
                .userId(userId)
                .businessId(businessId)
                .build();

        User user = User.builder().name("알바생").phoneNumber("010-1111-2222").build();
        ReflectionTestUtils.setField(user, "id", userId);

        User owner = User.builder().name("사장님").phoneNumber("010-9999-8888").build();
        Business business = Business.builder().bizName("테스트 사업체").owner(owner).build();
        ReflectionTestUtils.setField(business, "id", businessId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(businessRepository.findById(businessId)).willReturn(Optional.of(business));
        given(businessJoinRequestRepository.existsByUserAndBusinessAndStatusIn(
                eq(user), eq(business), any()
        )).willReturn(false);

        // when
        JoinBusinessResult result = joinBusinessUsecase.join(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        
        // DB 저장 확인
        ArgumentCaptor<BusinessJoinRequest> requestCaptor = ArgumentCaptor.forClass(BusinessJoinRequest.class);
        verify(businessJoinRequestRepository).save(requestCaptor.capture());
        BusinessJoinRequest savedRequest = requestCaptor.getValue();
        assertThat(savedRequest.getUser()).isEqualTo(user);
        assertThat(savedRequest.getBusiness()).isEqualTo(business);
        assertThat(savedRequest.getStatus()).isEqualTo(BusinessJoinRequestStatus.PENDING);

        // SMS 발송 확인
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(smsPushService).send(eq("010-9999-8888"), messageCaptor.capture());
        assertThat(messageCaptor.getValue()).contains("알바생", "테스트 사업체");
    }

    @Test
    @DisplayName("사업체 등록 요청 실패 - 유저 정보가 존재하지 않는 경우")
    void join_Fail_UserNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();
        JoinBusinessRequest request = JoinBusinessRequest.builder()
                .userId(userId)
                .businessId(businessId)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        JoinBusinessResult result = joinBusinessUsecase.join(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getMessage()).contains("User not found");
        verifyNoInteractions(businessRepository);
        verifyNoInteractions(businessJoinRequestRepository);
        verifyNoInteractions(smsPushService);
    }

    @Test
    @DisplayName("사업체 등록 요청 실패 - 비즈니스 정보가 존재하지 않는 경우")
    void join_Fail_BusinessNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();
        JoinBusinessRequest request = JoinBusinessRequest.builder()
                .userId(userId)
                .businessId(businessId)
                .build();

        User user = User.builder().build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(businessRepository.findById(businessId)).willReturn(Optional.empty());

        // when
        JoinBusinessResult result = joinBusinessUsecase.join(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getMessage()).contains("Business not found");
        verifyNoInteractions(businessJoinRequestRepository);
        verifyNoInteractions(smsPushService);
    }

    @Test
    @DisplayName("사업체 등록 요청 실패 - 이미 요청한 상태이거나 승인된 경우")
    void join_Fail_AlreadyRequested() {
        // given
        UUID userId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();
        JoinBusinessRequest request = JoinBusinessRequest.builder()
                .userId(userId)
                .businessId(businessId)
                .build();

        User user = User.builder().build();
        Business business = Business.builder().build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(businessRepository.findById(businessId)).willReturn(Optional.of(business));
        given(businessJoinRequestRepository.existsByUserAndBusinessAndStatusIn(
                eq(user), eq(business), any()
        )).willReturn(true); // 이미 요청함

        // when
        JoinBusinessResult result = joinBusinessUsecase.join(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getMessage()).contains("Already requested or approved");
        verifyNoInteractions(smsPushService);
    }
}
