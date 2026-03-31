package com.platform.sosangongin.cases.consent;

import com.platform.sosangongin.domains.consent.*;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.errors.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsentUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConsentPolicyRepository consentPolicyRepository;

    @Mock
    private UserConsentRepository userConsentRepository;

    @InjectMocks
    private ConsentUseCase consentUseCase;

    private UUID userId;
    private User user;
    private ConsentPolicy requiredPolicy;
    private ConsentPolicy optionalPolicy;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .name("테스트유저")
                .phoneNumber("010-1234-5678")
                .build();

        requiredPolicy = ConsentPolicy.builder()
                .id(1L)
                .title("서비스 이용약관")
                .content("서비스 이용약관 본문입니다.")
                .consentType(ConsentType.TERMS_OF_SERVICE)
                .isRequired(true)
                .version("1.0")
                .build();

        optionalPolicy = ConsentPolicy.builder()
                .id(2L)
                .title("마케팅 수신 동의")
                .content("마케팅 수신 동의 본문입니다.")
                .consentType(ConsentType.MARKETING)
                .isRequired(false)
                .version("1.0")
                .build();
    }

    @Test
    @DisplayName("미동의 약관 조회: 모든 약관이 미동의 상태인 경우 전체 목록 반환")
    void getPendingConsents_AllPending() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(consentPolicyRepository.findByIsActiveTrue()).willReturn(List.of(requiredPolicy, optionalPolicy));
        given(userConsentRepository.findByUser(user)).willReturn(List.of());

        // when
        PendingConsentResult result = consentUseCase.getPendingConsents(userId.toString());

        // then
        assertThat(result.getPendingConsents()).hasSize(2);
        assertThat(result.isAllAgreed()).isFalse();
    }

    @Test
    @DisplayName("미동의 약관 조회: 일부 약관에 동의한 경우 미동의 약관만 반환")
    void getPendingConsents_PartiallyAgreed() {
        // given
        UserConsent agreedConsent = UserConsent.builder()
                .user(user)
                .consentPolicy(requiredPolicy)
                .isAgreed(true)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(consentPolicyRepository.findByIsActiveTrue()).willReturn(List.of(requiredPolicy, optionalPolicy));
        given(userConsentRepository.findByUser(user)).willReturn(List.of(agreedConsent));

        // when
        PendingConsentResult result = consentUseCase.getPendingConsents(userId.toString());

        // then
        assertThat(result.getPendingConsents()).hasSize(1);
        assertThat(result.getPendingConsents().get(0).getConsentPolicyId()).isEqualTo(2L);
        assertThat(result.isAllAgreed()).isFalse();
    }

    @Test
    @DisplayName("미동의 약관 조회: 모든 약관에 동의한 경우 빈 목록과 allAgreed=true 반환")
    void getPendingConsents_AllAgreed() {
        // given
        UserConsent consent1 = UserConsent.builder().user(user).consentPolicy(requiredPolicy).isAgreed(true).build();
        UserConsent consent2 = UserConsent.builder().user(user).consentPolicy(optionalPolicy).isAgreed(true).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(consentPolicyRepository.findByIsActiveTrue()).willReturn(List.of(requiredPolicy, optionalPolicy));
        given(userConsentRepository.findByUser(user)).willReturn(List.of(consent1, consent2));

        // when
        PendingConsentResult result = consentUseCase.getPendingConsents(userId.toString());

        // then
        assertThat(result.getPendingConsents()).isEmpty();
        assertThat(result.isAllAgreed()).isTrue();
    }

    @Test
    @DisplayName("미동의 약관 조회: 존재하지 않는 사용자인 경우 예외 발생")
    void getPendingConsents_UserNotFound() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> consentUseCase.getPendingConsents(userId.toString()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("약관 동의: 최초 동의 시 UserConsent 생성 후 동의 처리")
    void agreeConsent_FirstTimeAgree() {
        // given
        AgreeConsentRequest request = AgreeConsentRequest.builder()
                .userId(userId.toString())
                .consentPolicyId(1L)
                .agreed(true)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(consentPolicyRepository.findById(1L)).willReturn(Optional.of(requiredPolicy));
        given(userConsentRepository.findByUserAndConsentPolicy(user, requiredPolicy)).willReturn(Optional.empty());

        // when
        AgreeConsentResult result = consentUseCase.agreeConsent(request);

        // then
        assertThat(result.isSuccess()).isTrue();
        verify(userConsentRepository).save(any(UserConsent.class));
    }

    @Test
    @DisplayName("약관 동의: 기존 동의 철회 후 재동의 처리")
    void agreeConsent_ReAgree() {
        // given
        UserConsent existingConsent = UserConsent.builder()
                .user(user)
                .consentPolicy(requiredPolicy)
                .isAgreed(false)
                .build();

        AgreeConsentRequest request = AgreeConsentRequest.builder()
                .userId(userId.toString())
                .consentPolicyId(1L)
                .agreed(true)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(consentPolicyRepository.findById(1L)).willReturn(Optional.of(requiredPolicy));
        given(userConsentRepository.findByUserAndConsentPolicy(user, requiredPolicy)).willReturn(Optional.of(existingConsent));

        // when
        AgreeConsentResult result = consentUseCase.agreeConsent(request);

        // then
        assertThat(result.isSuccess()).isTrue();
        verify(userConsentRepository).save(existingConsent);
    }

    @Test
    @DisplayName("약관 동의 철회: agreed=false로 요청 시 철회 처리")
    void agreeConsent_Revoke() {
        // given
        UserConsent existingConsent = UserConsent.builder()
                .user(user)
                .consentPolicy(optionalPolicy)
                .isAgreed(true)
                .build();

        AgreeConsentRequest request = AgreeConsentRequest.builder()
                .userId(userId.toString())
                .consentPolicyId(2L)
                .agreed(false)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(consentPolicyRepository.findById(2L)).willReturn(Optional.of(optionalPolicy));
        given(userConsentRepository.findByUserAndConsentPolicy(user, optionalPolicy)).willReturn(Optional.of(existingConsent));

        // when
        AgreeConsentResult result = consentUseCase.agreeConsent(request);

        // then
        assertThat(result.isSuccess()).isTrue();
        verify(userConsentRepository).save(existingConsent);
    }

    @Test
    @DisplayName("약관 동의: 존재하지 않는 약관에 동의 시 예외 발생")
    void agreeConsent_PolicyNotFound() {
        // given
        AgreeConsentRequest request = AgreeConsentRequest.builder()
                .userId(userId.toString())
                .consentPolicyId(999L)
                .agreed(true)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(consentPolicyRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> consentUseCase.agreeConsent(request))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
