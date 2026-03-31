package com.platform.sosangongin.cases.app.status;

import com.platform.sosangongin.domains.app.maintenance.AppMaintenance;
import com.platform.sosangongin.domains.app.maintenance.AppMaintenanceRepository;
import com.platform.sosangongin.domains.app.maintenance.MaintenanceStatus;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CheckAppStatusUsecaseTest {

    @InjectMocks
    private CheckAppStatusUsecase checkAppStatusUsecase;

    @Mock
    private AppMaintenanceRepository appMaintenanceRepository;

    @Mock
    private TimeGeneratorService timeGeneratorService;

    @Test
    @DisplayName("활성 점검이 없으면 underMaintenance=false를 반환한다")
    void check_noActiveMaintenance_shouldReturnNotUnderMaintenance() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 3, 27, 3, 0);
        given(timeGeneratorService.now()).willReturn(now);
        given(appMaintenanceRepository.findActiveAt(now))
                .willReturn(Optional.empty());

        // when
        CheckAppStatusResult result = checkAppStatusUsecase.check();

        // then
        assertThat(result.isUnderMaintenance()).isFalse();
        assertThat(result.getReason()).isNull();
        assertThat(result.getStartedAt()).isNull();
        assertThat(result.getEndedAt()).isNull();
    }

    @Test
    @DisplayName("활성 점검이 있으면 underMaintenance=true와 점검 정보를 반환한다")
    void check_activeMaintenance_shouldReturnMaintenanceInfo() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 3, 27, 3, 0);
        LocalDateTime startedAt = LocalDateTime.of(2026, 3, 27, 0, 0);
        LocalDateTime endedAt = LocalDateTime.of(2026, 3, 27, 6, 0);

        given(timeGeneratorService.now()).willReturn(now);

        AppMaintenance maintenance = AppMaintenance.builder()
                .status(MaintenanceStatus.IN_PROGRESS)
                .reason("서버 정기 점검")
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();

        given(appMaintenanceRepository.findActiveAt(now))
                .willReturn(Optional.of(maintenance));

        // when
        CheckAppStatusResult result = checkAppStatusUsecase.check();

        // then
        assertThat(result.isUnderMaintenance()).isTrue();
        assertThat(result.getReason()).isEqualTo("서버 정기 점검");
        assertThat(result.getStartedAt()).isEqualTo(startedAt);
        assertThat(result.getEndedAt()).isEqualTo(endedAt);
    }

    @Test
    @DisplayName("예약된 점검도 활성 상태로 반환된다")
    void check_scheduledMaintenance_shouldReturnMaintenanceInfo() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 3, 27, 3, 0);
        LocalDateTime startedAt = LocalDateTime.of(2026, 3, 27, 2, 0);
        LocalDateTime endedAt = LocalDateTime.of(2026, 3, 27, 4, 0);

        given(timeGeneratorService.now()).willReturn(now);

        AppMaintenance maintenance = AppMaintenance.builder()
                .status(MaintenanceStatus.SCHEDULED)
                .reason("긴급 핫픽스 배포")
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();

        given(appMaintenanceRepository.findActiveAt(now))
                .willReturn(Optional.of(maintenance));

        // when
        CheckAppStatusResult result = checkAppStatusUsecase.check();

        // then
        assertThat(result.isUnderMaintenance()).isTrue();
        assertThat(result.getReason()).isEqualTo("긴급 핫픽스 배포");
    }
}
