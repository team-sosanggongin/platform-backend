package com.platform.sosangongin.services.randoms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RandomCharGeneratorServiceTest {

    private final RandomCharGeneratorService randomCharGeneratorService = new RandomCharGeneratorServiceImpl();

    @Test
    @DisplayName("동일한 시드로 생성된 랜덤 문자열은 항상 같아야 한다 (결정적)")
    void deterministicRandomChar() {
        // given
        int digit = 10;
        String seed = "test-seed-123";

        // when
        String result1 = randomCharGeneratorService.getRandomChar(digit, seed);
        String result2 = randomCharGeneratorService.getRandomChar(digit, seed);

        // then
        assertThat(result1).isEqualTo(result2);
        assertThat(result1.length()).isEqualTo(digit);
    }

    @Test
    @DisplayName("다른 시드로 생성된 랜덤 문자열은 달라야 한다")
    void differentSeedDifferentRandomChar() {
        // given
        int digit = 10;
        String seed1 = "seed-A";
        String seed2 = "seed-B";

        // when
        String result1 = randomCharGeneratorService.getRandomChar(digit, seed1);
        String result2 = randomCharGeneratorService.getRandomChar(digit, seed2);

        // then
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    @DisplayName("숫자만 포함된 랜덤 문자열 생성 테스트")
    void deterministicRandomNumber() {
        // given
        int digit = 6;
        Integer seed = 12345;

        // when
        String result = randomCharGeneratorService.getRandomNumber(digit, seed);

        // then
        assertThat(result).matches("^[0-9]+$");
        assertThat(result.length()).isEqualTo(digit);
    }

    @Test
    @DisplayName("영숫자 랜덤 문자열 생성 테스트")
    void randomCharContainsAlphanumeric() {
        // given
        int digit = 20;
        Object seed = new Object(); // Object의 hashCode 사용

        // when
        String result = randomCharGeneratorService.getRandomChar(digit, seed);

        // then
        assertThat(result).matches("^[a-zA-Z0-9]+$");
        assertThat(result.length()).isEqualTo(digit);
    }

    @Test
    @DisplayName("자릿수가 0 이하일 경우 예외 발생")
    void invalidDigitThrowsException() {
        // given
        int invalidDigit = 0;
        String seed = "seed";

        // when & then
        assertThatThrownBy(() -> randomCharGeneratorService.getRandomChar(invalidDigit, seed))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Seed가 null일 경우 매번 다른 값이 생성되어야 한다")
    void nullSeedGeneratesDifferentValues() {
        // given
        int digit = 10;
        Object seed = null;

        // when
        String result1 = randomCharGeneratorService.getRandomChar(digit, seed);
        String result2 = randomCharGeneratorService.getRandomChar(digit, seed);

        // then
        // 확률적으로 같을 수 있지만 매우 희박함
        assertThat(result1).isNotEqualTo(result2);
    }
}
