package com.platform.sosangongin.cases.notices;

import com.platform.sosangongin.domains.notices.NoticeStatus;
import com.platform.sosangongin.domains.notices.PublicNotice;
import com.platform.sosangongin.domains.notices.PublicNoticeRepository;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicNoticeUseCaseTest {

    @Mock
    private PublicNoticeRepository noticeRepository;

    @Mock
    private TimeGeneratorService timeGeneratorService;

    @InjectMocks
    private PublicNoticeUseCase publicNoticeUseCase;

    private LocalDateTime fixedNow;

    @BeforeEach
    void setUp() {
        // 테스트 기준 시간을 2026년 3월 16일 13시 0분으로 고정
        fixedNow = LocalDateTime.of(2026, 3, 16, 13, 0);
    }

    @Test
    @DisplayName("공지사항 목록 조회: 게시 조건(상태, 시간)이 올바르게 전달되는지 확인")
    void getNoticeList_ValidFlow() {
        // given
        PublicNoticeRequest request = new PublicNoticeRequest(0, 10);
        Pageable pageable = PageRequest.of(0, 10);

        PublicNotice mockNotice = createNotice(1L, "공지", NoticeStatus.PUBLISHED, fixedNow.minusHours(1), null);
        Page<PublicNotice> noticePage = new PageImpl<>(List.of(mockNotice));

        given(timeGeneratorService.now()).willReturn(fixedNow);
        given(noticeRepository.findActiveNotices(fixedNow, pageable)).willReturn(noticePage);

        // when
        PublicNoticeResult result = publicNoticeUseCase.getNoticeList(request);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getPublicNotices().getContent()).hasSize(1);
        verify(noticeRepository).findActiveNotices(fixedNow, pageable);
    }

    @Test
    @DisplayName("상세 조회 성공: 조회수 증가가 도메인 객체에서 실행되는지 확인")
    void getNoticeDetail_IncreaseViewCount() {
        // given
        Long noticeId = 1L;
        // spy를 사용하여 실제 객체의 메서드 호출을 추적
        PublicNotice realNotice = createNotice(noticeId, "제목", NoticeStatus.PUBLISHED, fixedNow.minusDays(1), null);
        PublicNotice spyNotice = spy(realNotice);
        long initialViewCount = spyNotice.getViewCount();

        given(noticeRepository.findById(noticeId)).willReturn(Optional.of(spyNotice));
        given(timeGeneratorService.now()).willReturn(fixedNow);

        // when
        PublicNoticeDetailResult result = publicNoticeUseCase.getNoticeDetail(noticeId);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getPublicNotice().viewCount()).isEqualTo(initialViewCount + 1);

        // 중요: 도메인 메서드가 호출되었는지 검증 (더티 체킹에 의해 DB 반영 예정)
        verify(spyNotice, times(1)).incrementViewCount();
    }

    @Test
    @DisplayName("상세 조회 실패: 게시 시작 전(예약) 공지에 접근할 경우 BAD_REQUEST")
    void getNoticeDetail_ForbiddenByTime() {
        // given
        Long noticeId = 1L;
        // 1시간 뒤에 시작되는 예약 공지
        PublicNotice futureNotice = createNotice(noticeId, "예약공지", NoticeStatus.PUBLISHED, fixedNow.plusHours(1), null);

        given(noticeRepository.findById(noticeId)).willReturn(Optional.of(futureNotice));
        given(timeGeneratorService.now()).willReturn(fixedNow);

        // when
        PublicNoticeDetailResult result = publicNoticeUseCase.getNoticeDetail(noticeId);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getMessage()).contains("you can`t access");
    }

    @Test
    @DisplayName("상세 조회 실패: 존재하지 않는 ID로 조회 시 NOT_FOUND")
    void getNoticeDetail_NotFound() {
        // given
        Long noticeId = 999L;
        given(noticeRepository.findById(noticeId)).willReturn(Optional.empty());

        // when
        PublicNoticeDetailResult result = publicNoticeUseCase.getNoticeDetail(noticeId);

        // then
        assertThat(result.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    /**
     * 테스트용 공지사항 엔티티 생성 헬퍼
     */
    private PublicNotice createNotice(Long id, String title, NoticeStatus status, LocalDateTime startsAt, LocalDateTime endsAt) {
        return PublicNotice.builder()
                .id(id)
                .title(title)
                .content("본문 내용")
                .status(status)
                .startsAt(startsAt)
                .endsAt(endsAt)
                .viewCount(0L)
                .build();
    }
}