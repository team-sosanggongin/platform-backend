package com.platform.sosangongin.services.times;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class TimeGeneratorServiceTest {

    private final TimeGeneratorService timeGeneratorService = new TimeGeneratorServiceImpl();

    @Test
    @DisplayName("현재 시간 생성 테스트")
    void nowTest() {
        LocalDateTime now = timeGeneratorService.now();
        assertThat(now).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
    }

    @Test
    @DisplayName("특정 시간 뒤 계산 테스트 (5분 뒤)")
    void afterTest() {
        long amount = 5;
        ChronoUnit unit = ChronoUnit.MINUTES;

        LocalDateTime result = timeGeneratorService.after(amount, unit);
        LocalDateTime expected = LocalDateTime.now().plus(amount, unit);

        assertThat(result).isCloseTo(expected, within(1, ChronoUnit.SECONDS));
    }

    @Test
    @DisplayName("특정 시간 전 계산 테스트 (1일 전)")
    void beforeTest() {
        long amount = 1;
        ChronoUnit unit = ChronoUnit.DAYS;

        LocalDateTime result = timeGeneratorService.before(amount, unit);
        LocalDateTime expected = LocalDateTime.now().minus(amount, unit);

        assertThat(result).isCloseTo(expected, within(1, ChronoUnit.SECONDS));
    }
}
