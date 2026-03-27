package com.platform.sosangongin.cases.app;

import com.platform.sosangongin.domains.app.*;
import com.platform.sosangongin.domains.common.ClientPlatform;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CheckAppStatusUsecaseTest {

    @Mock
    private AppMaintenanceRepository maintenanceRepository;

    @Mock
    private AppVersionRepository versionRepository;

    @Mock
    private TimeGeneratorService timeGeneratorService;

    @InjectMocks
    private CheckAppStatusUsecase checkAppStatusUsecase;

    @Test
    @DisplayName("점검 없고 버전 호환 시 serviceAvailable = true")
    void check_noMaintenance_supportedVersion() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 3, 23, 12, 0);
        given(timeGeneratorService.now()).willReturn(now);
        given(maintenanceRepository.findActiveAt(now)).willReturn(Optional.empty());

        AppVersion latestVersion = AppVersion.builder()
                .platform(ClientPlatform.ANDROID)
                .versionName("2.0.0")
                .versionCode(20)
                .minSupportedVersionCode(10)
                .forceUpdate(false)
                .build();
        given(versionRepository.findTopByPlatformOrderByVersionCodeDesc(ClientPlatform.ANDROID))
                .willReturn(Optional.of(latestVersion));
        given(versionRepository.existsByPlatformAndMinSupportedVersionCodeLessThanEqual(ClientPlatform.ANDROID, 15))
                .willReturn(true);

        CheckAppStatusRequest request = CheckAppStatusRequest.builder()
                .platform(ClientPlatform.ANDROID)
                .versionCode(15)
                .build();

        // when
        CheckAppStatusResult result = checkAppStatusUsecase.check(request);

        // then
        assertThat(result.isServiceAvailable()).isTrue();
        assertThat(result.getMaintenance().isActive()).isFalse();
        assertThat(result.getVersion().isSupported()).isTrue();
        assertThat(result.getVersion().isForceUpdateRequired()).isFalse();
    }

    @Test
    @DisplayName("점검 중이면 serviceAvailable = false")
    void check_activeMaintenance() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 3, 23, 3, 0);
        given(timeGeneratorService.now()).willReturn(now);

        AppMaintenance maintenance = AppMaintenance.builder()
                .status(MaintenanceStatus.IN_PROGRESS)
                .reason("서버 업데이트")
                .startedAt(LocalDateTime.of(2026, 3, 23, 2, 0))
                .endedAt(LocalDateTime.of(2026, 3, 23, 5, 0))
                .build();
        given(maintenanceRepository.findActiveAt(now)).willReturn(Optional.of(maintenance));

        given(versionRepository.findTopByPlatformOrderByVersionCodeDesc(ClientPlatform.ANDROID))
                .willReturn(Optional.empty());

        CheckAppStatusRequest request = CheckAppStatusRequest.builder()
                .platform(ClientPlatform.ANDROID)
                .versionCode(10)
                .build();

        // when
        CheckAppStatusResult result = checkAppStatusUsecase.check(request);

        // then
        assertThat(result.isServiceAvailable()).isFalse();
        assertThat(result.getMaintenance().isActive()).isTrue();
        assertThat(result.getMaintenance().getReason()).isEqualTo("서버 업데이트");
        assertThat(result.getMaintenance().getStartedAt()).isEqualTo(LocalDateTime.of(2026, 3, 23, 2, 0));
        assertThat(result.getMaintenance().getEndedAt()).isEqualTo(LocalDateTime.of(2026, 3, 23, 5, 0));
    }

    @Test
    @DisplayName("미지원 버전이면 serviceAvailable = false")
    void check_unsupportedVersion() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 3, 23, 12, 0);
        given(timeGeneratorService.now()).willReturn(now);
        given(maintenanceRepository.findActiveAt(now)).willReturn(Optional.empty());

        AppVersion latestVersion = AppVersion.builder()
                .platform(ClientPlatform.IOS)
                .versionName("3.0.0")
                .versionCode(30)
                .minSupportedVersionCode(20)
                .forceUpdate(true)
                .build();
        given(versionRepository.findTopByPlatformOrderByVersionCodeDesc(ClientPlatform.IOS))
                .willReturn(Optional.of(latestVersion));
        given(versionRepository.existsByPlatformAndMinSupportedVersionCodeLessThanEqual(ClientPlatform.IOS, 5))
                .willReturn(false);

        CheckAppStatusRequest request = CheckAppStatusRequest.builder()
                .platform(ClientPlatform.IOS)
                .versionCode(5)
                .build();

        // when
        CheckAppStatusResult result = checkAppStatusUsecase.check(request);

        // then
        assertThat(result.isServiceAvailable()).isFalse();
        assertThat(result.getVersion().isSupported()).isFalse();
        assertThat(result.getVersion().isForceUpdateRequired()).isTrue();
    }

    @Test
    @DisplayName("미지원 버전이지만 강제 업데이트 비활성이면 forceUpdateRequired = false")
    void check_unsupportedVersion_noForceUpdate() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 3, 23, 12, 0);
        given(timeGeneratorService.now()).willReturn(now);
        given(maintenanceRepository.findActiveAt(now)).willReturn(Optional.empty());

        AppVersion latestVersion = AppVersion.builder()
                .platform(ClientPlatform.ANDROID)
                .versionName("3.0.0")
                .versionCode(30)
                .minSupportedVersionCode(20)
                .forceUpdate(false)
                .build();
        given(versionRepository.findTopByPlatformOrderByVersionCodeDesc(ClientPlatform.ANDROID))
                .willReturn(Optional.of(latestVersion));
        given(versionRepository.existsByPlatformAndMinSupportedVersionCodeLessThanEqual(ClientPlatform.ANDROID, 5))
                .willReturn(false);

        CheckAppStatusRequest request = CheckAppStatusRequest.builder()
                .platform(ClientPlatform.ANDROID)
                .versionCode(5)
                .build();

        // when
        CheckAppStatusResult result = checkAppStatusUsecase.check(request);

        // then
        assertThat(result.isServiceAvailable()).isFalse();
        assertThat(result.getVersion().isSupported()).isFalse();
        assertThat(result.getVersion().isForceUpdateRequired()).isFalse();
    }

    @Test
    @DisplayName("하위 호환 - 최신 버전의 minSupported보다 낮아도 이전 버전이 지원하면 supported = true")
    void check_backwardCompatible() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 3, 23, 12, 0);
        given(timeGeneratorService.now()).willReturn(now);
        given(maintenanceRepository.findActiveAt(now)).willReturn(Optional.empty());

        // 최신 버전은 v3 (minSupported=20)이지만, v2 레코드(minSupported=5)가 존재
        AppVersion latestVersion = AppVersion.builder()
                .platform(ClientPlatform.ANDROID)
                .versionName("3.0.0")
                .versionCode(30)
                .minSupportedVersionCode(20)
                .forceUpdate(false)
                .build();
        given(versionRepository.findTopByPlatformOrderByVersionCodeDesc(ClientPlatform.ANDROID))
                .willReturn(Optional.of(latestVersion));
        // 클라이언트 versionCode=10은 v2 레코드(minSupported=5)에 의해 지원됨
        given(versionRepository.existsByPlatformAndMinSupportedVersionCodeLessThanEqual(ClientPlatform.ANDROID, 10))
                .willReturn(true);

        CheckAppStatusRequest request = CheckAppStatusRequest.builder()
                .platform(ClientPlatform.ANDROID)
                .versionCode(10)
                .build();

        // when
        CheckAppStatusResult result = checkAppStatusUsecase.check(request);

        // then
        assertThat(result.isServiceAvailable()).isTrue();
        assertThat(result.getVersion().isSupported()).isTrue();
        assertThat(result.getVersion().getLatestVersionCode()).isEqualTo(30);
    }

    @Test
    @DisplayName("버전 정보가 없으면 version = null, serviceAvailable = true")
    void check_noVersionInfo() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 3, 23, 12, 0);
        given(timeGeneratorService.now()).willReturn(now);
        given(maintenanceRepository.findActiveAt(now)).willReturn(Optional.empty());
        given(versionRepository.findTopByPlatformOrderByVersionCodeDesc(ClientPlatform.IOS))
                .willReturn(Optional.empty());

        CheckAppStatusRequest request = CheckAppStatusRequest.builder()
                .platform(ClientPlatform.IOS)
                .versionCode(1)
                .build();

        // when
        CheckAppStatusResult result = checkAppStatusUsecase.check(request);

        // then
        assertThat(result.isServiceAvailable()).isTrue();
        assertThat(result.getVersion()).isNull();
    }

    @Test
    @DisplayName("점검 중이고 미지원 버전이면 serviceAvailable = false")
    void check_maintenanceAndUnsupported() {
        // given
        LocalDateTime now = LocalDateTime.of(2026, 3, 23, 3, 0);
        given(timeGeneratorService.now()).willReturn(now);

        AppMaintenance maintenance = AppMaintenance.builder()
                .status(MaintenanceStatus.SCHEDULED)
                .reason("DB 마이그레이션")
                .startedAt(LocalDateTime.of(2026, 3, 23, 2, 0))
                .endedAt(LocalDateTime.of(2026, 3, 23, 6, 0))
                .build();
        given(maintenanceRepository.findActiveAt(now)).willReturn(Optional.of(maintenance));

        AppVersion latestVersion = AppVersion.builder()
                .platform(ClientPlatform.ANDROID)
                .versionName("2.0.0")
                .versionCode(20)
                .minSupportedVersionCode(15)
                .forceUpdate(true)
                .build();
        given(versionRepository.findTopByPlatformOrderByVersionCodeDesc(ClientPlatform.ANDROID))
                .willReturn(Optional.of(latestVersion));
        given(versionRepository.existsByPlatformAndMinSupportedVersionCodeLessThanEqual(ClientPlatform.ANDROID, 5))
                .willReturn(false);

        CheckAppStatusRequest request = CheckAppStatusRequest.builder()
                .platform(ClientPlatform.ANDROID)
                .versionCode(5)
                .build();

        // when
        CheckAppStatusResult result = checkAppStatusUsecase.check(request);

        // then
        assertThat(result.isServiceAvailable()).isFalse();
        assertThat(result.getMaintenance().isActive()).isTrue();
        assertThat(result.getVersion().isSupported()).isFalse();
        assertThat(result.getVersion().isForceUpdateRequired()).isTrue();
    }
}
