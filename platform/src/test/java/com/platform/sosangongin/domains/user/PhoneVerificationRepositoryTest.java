package com.platform.sosangongin.domains.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PhoneVerificationRepositoryTest {

    @Autowired
    private PhoneVerificationRepository phoneVerificationRepository;

    @Test
    @DisplayName("휴대전화 인증 정보 저장 테스트")
    void savePhoneVerification() {
        // given
        String phoneNumber = "010-1234-5678";
        String code = "123456";
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(3);
        PhoneVerification verification = new PhoneVerification(phoneNumber, code, expiredAt);

        // when
        PhoneVerification savedVerification = phoneVerificationRepository.save(verification);

        // then
        assertThat(savedVerification.getId()).isNotNull();
        assertThat(savedVerification.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(savedVerification.getCode()).isEqualTo(code);
        assertThat(savedVerification.getStatus()).isEqualTo(VerificationStatus.PENDING);
        assertThat(savedVerification.getExpiredAt()).isEqualTo(expiredAt);
    }

    @Test
    @DisplayName("인증 성공 시 상태 변경 테스트")
    void verifySuccess() {
        // given
        PhoneVerification verification = new PhoneVerification("010-1234-5678", "123456", LocalDateTime.now().plusMinutes(3));
        phoneVerificationRepository.save(verification);

        // when
        boolean result = verification.verify("123456");

        // then
        assertThat(result).isTrue();
        assertThat(verification.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    @DisplayName("인증 코드 불일치 시 실패 테스트")
    void verifyFailCodeMismatch() {
        // given
        PhoneVerification verification = new PhoneVerification("010-1234-5678", "123456", LocalDateTime.now().plusMinutes(3));
        phoneVerificationRepository.save(verification);

        // when
        boolean result = verification.verify("654321");

        // then
        assertThat(result).isFalse();
        assertThat(verification.getStatus()).isEqualTo(VerificationStatus.PENDING);
    }

    @Test
    @DisplayName("만료된 인증 시도 시 상태 변경 테스트")
    void verifyExpired() {
        // given
        LocalDateTime expiredAt = LocalDateTime.now().minusMinutes(1);
        PhoneVerification verification = new PhoneVerification("010-1234-5678", "123456", expiredAt);
        phoneVerificationRepository.save(verification);

        // when
        boolean isExpired = verification.isExpired(LocalDateTime.now());

        // then
        Assertions.assertTrue(isExpired);
        assertThat(verification.getStatus()).isEqualTo(VerificationStatus.EXPIRED);
    }
}
