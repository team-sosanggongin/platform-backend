package com.platform.sosangongin.domains.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("User 저장 성공 테스트")
    void saveUserSuccess() {
        // given
        User user = new User("010-1234-5678", "Test User");

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(savedUser.getName()).isEqualTo("Test User");
    }

    @Test
    @DisplayName("User 전화번호 중복 저장 실패 테스트")
    void saveUserDuplicatePhoneNumberFail() {
        // given
        User user1 = new User("010-1234-5678", "User 1");
        userRepository.save(user1);

        User user2 = new User("010-1234-5678", "User 2");

        // when & then
        assertThatThrownBy(() -> {
            userRepository.save(user2);
            userRepository.flush(); // 강제로 DB에 반영하여 제약 조건 위반 발생 유도
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("User 전화번호로 조회 테스트")
    void findByPhoneNumberSuccess() {
        // given
        String phoneNumber = "010-9876-5432";
        User user = new User(phoneNumber, "Search User");
        userRepository.save(user);

        // when
        User foundUser = userRepository.findByPhoneNumber(phoneNumber).orElse(null);

        // then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getPhoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    @DisplayName("최초 생성된 User는 휴대전화 인증이 완료되지 않은 상태이다")
    void initialUserShouldHaveUnverifiedPhone() {
        // given
        User user = new User("010-1111-2222", "New User");

        // when
        User savedUser = userRepository.save(user);

        // then
        assertThat(savedUser.isPhoneVerified()).isFalse();
        assertThat(savedUser.getPhoneVerifiedAt()).isNull();
    }

    @Test
    @DisplayName("휴대전화 인증 성공 시 인증 정보가 저장된다")
    void phoneVerificationShouldUpdateStatus() {
        // given
        User user = new User("01033334444", "Verify User");
        userRepository.saveAndFlush(user);

        // when
        User foundUser = userRepository.findById(user.getId()).orElseThrow();
        foundUser.verifyPhone();
        User updatedUser = userRepository.saveAndFlush(foundUser);

        // then
        assertThat(updatedUser.isPhoneVerified()).isTrue();
        assertThat(updatedUser.getPhoneVerifiedAt()).isNotNull();
    }
}
