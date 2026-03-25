package com.backoffice.sosangongin.gateways.platform;

import com.backoffice.sosangongin.dto.notice.CreateNoticeRequest;
import com.backoffice.sosangongin.dto.notice.NoticeResponse;
import com.backoffice.sosangongin.dto.notice.UpdateNoticeRequest;
import com.backoffice.sosangongin.gateways.PlatformNoticeGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PlatformNoticeGatewayImpl implements PlatformNoticeGateway {

    @Override
    public NoticeResponse createNotice(CreateNoticeRequest request) {
        // TODO: 플랫폼의 공지 생성 API (POST /internal/api/notices) 호출 로직 구현 예정
        return null;
    }

    @Override
    public NoticeResponse getNotice(UUID noticeId) {
        // TODO: 플랫폼의 공지 상세 조회 API (GET /internal/api/notices/{id}) 호출 로직 구현 예정
        return null;
    }

    @Override
    public List<NoticeResponse> getAllNotices() {
        // TODO: 플랫폼의 공지 목록 조회 API (GET /internal/api/notices) 호출 로직 구현 예정
        return null;
    }

    @Override
    public void updateNotice(UUID noticeId, UpdateNoticeRequest request) {
        // TODO: 플랫폼의 공지 수정 API (PUT /internal/api/notices/{id}) 호출 로직 구현 예정
    }

    @Override
    public void deleteNotice(UUID noticeId) {
        // TODO: 플랫폼의 공지 삭제 API (DELETE /internal/api/notices/{id}) 호출 로직 구현 예정
    }
}
