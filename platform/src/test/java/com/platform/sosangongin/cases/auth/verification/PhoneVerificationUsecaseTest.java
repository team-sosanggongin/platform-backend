package com.platform.sosangongin.cases.auth.verification;

import com.platform.sosangongin.domains.user.PhoneVerification;
import com.platform.sosangongin.domains.user.PhoneVerificationRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.services.randoms.RandomCharGeneratorService;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
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
    @DisplayName("인증되지 않은 사용자의 전화번호 인증 요청 성공")
    void handlePhoneVerification_Success_ForUnverifiedUser() {
        // given
        UUID userId = UUID.randomUUID();
        User unverifiedUser = new User("010-1234-5678", "Test User");
        PhoneVerificationRequest request = new PhoneVerificationRequest(userId.toString(), "code");

        String randomCode = "12345";
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        given(userRepository.findById(userId)).willReturn(Optional.of(unverifiedUser));
        given(randomCharGeneratorService.getRandomNumber(eq(5), anyString())).willReturn(randomCode);
        given(timeGeneratorService.after(5, ChronoUnit.MINUTES)).willReturn(expiryTime);

        // when
        PhoneVerificationResult result = phoneVerificationUsecase.handlePhoneVerification(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);

        // PhoneVerification 객체가 올바르게 생성되어 저장되는지 확인
        ArgumentCaptor<PhoneVerification> captor = ArgumentCaptor.forClass(PhoneVerification.class);
        verify(phoneVerificationRepository).save(captor.capture());
        PhoneVerification savedVerification = captor.getValue();

        assertThat(savedVerification.getUser()).isEqualTo(unverifiedUser);
        assertThat(savedVerification.getCode()).isEqualTo(randomCode);
        assertThat(savedVerification.getExpiredAt()).isEqualTo(expiryTime);
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 전화번호 인증 요청 실패")
    void handlePhoneVerification_Fail_WhenUserNotFound() {
        // given
        UUID userId = UUID.randomUUID();
        PhoneVerificationRequest request = new PhoneVerificationRequest(userId.toString(), "code");

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when
        PhoneVerificationResult result = phoneVerificationUsecase.handlePhoneVerification(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getMessage()).contains("user is not registered");
        verify(phoneVerificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 인증된 사용자의 전화번호 인증 요청 실패")
    void handlePhoneVerification_Fail_WhenUserAlreadyVerified() {
        // given
        UUID userId = UUID.randomUUID();
        // User 생성 시 자동으로 isPhoneVerified=false가 될 것이나, 명확성을 위해 새로운 User 생성 후 verifyPhone 호출
        User verifiedUser = new User("010-1234-5678", "Verified User");
        verifiedUser.verifyPhone(); // 사용자를 인증된 상태로 만듦
        
        PhoneVerificationRequest request = new PhoneVerificationRequest(userId.toString(), "code");

        given(userRepository.findById(userId)).willReturn(Optional.of(verifiedUser));

        // when
        PhoneVerificationResult result = phoneVerificationUsecase.handlePhoneVerification(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getMessage()).contains("already verified");
        verify(phoneVerificationRepository, never()).save(any());
    }
}
