package com.platform.sosangongin.cases.notices.admin;

import com.platform.sosangongin.domains.notices.NoticeStatus;
import com.platform.sosangongin.domains.notices.PublicNotice;
import com.platform.sosangongin.domains.notices.PublicNoticeRepository;
import com.platform.sosangongin.services.push.PushProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminNoticeManageUseCaseTest {

    @Mock
    private PublicNoticeRepository publicNoticeRepository;

    @Mock
    private PushProvider pushProvider;

    @InjectMocks
    private AdminNoticeManageUseCase adminNoticeManageUseCase;

    @Test
    @DisplayName("공지사항 생성 시 DB에 저장되고, push 옵션이 true면 동기적으로 푸시를 발송한다")
    void createNotice_WithPushTrue_ShouldSaveAndSendPush() {
        // given
        AdminNoticeCreateRequest request = createRequest(true);
        PublicNotice savedNotice = PublicNotice.builder()
                .id(1L)
                .title("테스트 공지")
                .content("내용")
                .status(NoticeStatus.PUBLISHED)
                .startsAt(LocalDateTime.now())
                .build();
        
        given(publicNoticeRepository.save(any(PublicNotice.class))).willReturn(savedNotice);

        // when
        Long noticeId = adminNoticeManageUseCase.createNotice(request);

        // then
        assert(noticeId.equals(1L));
        verify(publicNoticeRepository).save(any(PublicNotice.class));
        verify(pushProvider).sendToTopic(eq("all_users"), anyString(), eq("테스트 공지")); // 푸시 발송됨
    }

    @Test
    @DisplayName("공지사항 생성 시 push 옵션이 false면 푸시가 발송되지 않는다")
    void createNotice_WithPushFalse_ShouldSaveOnly() {
        // given
        AdminNoticeCreateRequest request = createRequest(false);
        PublicNotice savedNotice = PublicNotice.builder()
                .id(2L)
                .title("푸시 안보내는 공지")
                .content("내용")
                .status(NoticeStatus.PUBLISHED)
                .startsAt(LocalDateTime.now())
                .build();
        
        given(publicNoticeRepository.save(any(PublicNotice.class))).willReturn(savedNotice);

        // when
        Long noticeId = adminNoticeManageUseCase.createNotice(request);

        // then
        assert(noticeId.equals(2L));
        verify(publicNoticeRepository).save(any(PublicNotice.class));
        verify(pushProvider, never()).sendToTopic(anyString(), anyString(), anyString()); // 푸시 발송 안 됨
    }

    // 헬퍼 메서드: 리플렉션을 사용하여 DTO 강제 생성
    private AdminNoticeCreateRequest createRequest(boolean isSendPush) {
        AdminNoticeCreateRequest request = new AdminNoticeCreateRequest();
        ReflectionTestUtils.setField(request, "title", "테스트 공지");
        ReflectionTestUtils.setField(request, "content", "테스트 내용");
        ReflectionTestUtils.setField(request, "authorName", "어드민");
        ReflectionTestUtils.setField(request, "startsAt", LocalDateTime.now());
        ReflectionTestUtils.setField(request, "sendPush", isSendPush);
        return request;
    }
}
