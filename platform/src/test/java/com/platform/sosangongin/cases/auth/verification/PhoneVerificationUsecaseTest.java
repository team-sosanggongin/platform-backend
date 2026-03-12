package com.platform.sosangongin.cases.auth.verification;

import com.platform.sosangongin.domains.user.*;
import com.platform.sosangongin.services.randoms.RandomCharGeneratorService;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PhoneVerificationUsecaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RandomCharGeneratorService randomCharGeneratorService;
    @Mock
    private TimeGeneratorService timeGeneratorService;
    @Mock
    private PhoneVerificationRepository phoneVerificationRepository;

    @InjectMocks
    private PhoneVerificationUsecase phoneVerificationUsecase;

    @Test
    @DisplayName("전화번호 인증 확인 요청 성공")
    void verifyPhoneSuccess() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("010-1234-5678", "Test User");
        String code = "12345";
        PhoneVerificationRequest request = new PhoneVerificationRequest(true, userId.toString(), code);

        PhoneVerification verification = PhoneVerification.builder()
                .user(user)
                .code(code)
                .status(VerificationStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(phoneVerificationRepository.findTopByUserOrderByCreatedAtDesc(user)).willReturn(Optional.of(verification));
        given(timeGeneratorService.now()).willReturn(LocalDateTime.now());

        // when
        PhoneVerificationResult result = phoneVerificationUsecase.handlePhoneVerification(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(user.isPhoneVerified()).isTrue();
        assertThat(verification.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    @DisplayName("인증 이력이 없는 경우 실패")
    void verifyPhoneFailNoHistory() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("010-1234-5678", "Test User");
        PhoneVerificationRequest request = new PhoneVerificationRequest(true, userId.toString(), "12345");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(phoneVerificationRepository.findTopByUserOrderByCreatedAtDesc(user)).willReturn(Optional.empty());

        // when
        PhoneVerificationResult result = phoneVerificationUsecase.handlePhoneVerification(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getMessage()).contains("history is not present");
    }

    @Test
    @DisplayName("이미 만료된 인증 요청 실패")
    void verifyPhoneFailExpired() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("010-1234-5678", "Test User");
        PhoneVerificationRequest request = new PhoneVerificationRequest(true, userId.toString(), "12345");

        PhoneVerification verification = PhoneVerification.builder()
                .user(user)
                .code("12345")
                .status(VerificationStatus.PENDING)
                .expiredAt(LocalDateTime.now().minusMinutes(1)) // 만료됨
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(phoneVerificationRepository.findTopByUserOrderByCreatedAtDesc(user)).willReturn(Optional.of(verification));
        given(timeGeneratorService.now()).willReturn(LocalDateTime.now());

        // when
        PhoneVerificationResult result = phoneVerificationUsecase.handlePhoneVerification(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getMessage()).contains("expired");
        assertThat(verification.getStatus()).isEqualTo(VerificationStatus.EXPIRED);
    }

    @Test
    @DisplayName("잘못된 인증 코드 실패")
    void verifyPhoneFailInvalidCode() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("010-1234-5678", "Test User");
        PhoneVerificationRequest request = new PhoneVerificationRequest(true, userId.toString(), "wrong_code");

        PhoneVerification verification = PhoneVerification.builder()
                .user(user)
                .code("12345")
                .status(VerificationStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(phoneVerificationRepository.findTopByUserOrderByCreatedAtDesc(user)).willReturn(Optional.of(verification));
        given(timeGeneratorService.now()).willReturn(LocalDateTime.now());

        // when
        PhoneVerificationResult result = phoneVerificationUsecase.handlePhoneVerification(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getMessage()).contains("code is invalid");
    }

    @Test
    @DisplayName("이미 처리된 인증 요청 실패")
    void verifyPhoneFailAlreadyProcessed() {
        // given
        UUID userId = UUID.randomUUID();
        User user = new User("010-1234-5678", "Test User");
        PhoneVerificationRequest request = new PhoneVerificationRequest(true, userId.toString(), "12345");

        PhoneVerification verification = PhoneVerification.builder()
                .user(user)
                .code("12345")
                .status(VerificationStatus.VERIFIED) // 이미 완료됨
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(phoneVerificationRepository.findTopByUserOrderByCreatedAtDesc(user)).willReturn(Optional.of(verification));

        // when
        PhoneVerificationResult result = phoneVerificationUsecase.handlePhoneVerification(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getMessage()).contains("illegal state");
    }
}
