package com.platform.sosangongin.cases.notices.admin;

import com.platform.sosangongin.domains.notices.NoticeStatus;
import com.platform.sosangongin.domains.notices.PublicNotice;
import com.platform.sosangongin.domains.notices.PublicNoticeRepository;
import com.platform.sosangongin.services.push.PushProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminNoticeManageUseCase {
    private final PublicNoticeRepository publicNoticeRepository;
    private final PushProvider pushProvider;

    @Transactional
    public Long createNotice(AdminNoticeCreateRequest request) {
        PublicNotice notice = PublicNotice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .authorName(request.getAuthorName())
                .startsAt(request.getStartsAt())
                .endsAt(request.getEndsAt())
                .isPinned(request.isPinned())
                .status(NoticeStatus.PUBLISHED) // "관리자가 바로 발행"으로 가정
                .build();
        PublicNotice savedNotice = publicNoticeRepository.save(notice);
        log.info("새 공지사항이 생성되었습니다. feat.{}", savedNotice.getId());

        // 푸시 발송 여부가 체크되어 있다면 전체 사용자에게 발송
        if (request.isSendPush()) {
            try {
                // TODO: 향후 "all_users" 토픽 상수를 공통으로 분리 고려
                pushProvider.sendToTopic("all_users", "새로운 공지사항 안내", notice.getTitle());
                log.info("공지사항이 Push 되었습니다. {}", savedNotice.getId());
            } catch (Exception e) {
                // 푸시 발송 실패가 공지사항 등록 자체를 롤백시키지 않도록 예외 처리
                log.error("Failed to send push notification for notice ID: {}", savedNotice.getId(), e);
            }
        }
        return savedNotice.getId();
    }
}
