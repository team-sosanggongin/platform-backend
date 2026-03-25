package com.backoffice.sosangongin.gateways;

import com.backoffice.sosangongin.dto.notice.CreateNoticeRequest;
import com.backoffice.sosangongin.dto.notice.NoticeResponse;
import com.backoffice.sosangongin.dto.notice.UpdateNoticeRequest;

import java.util.List;
import java.util.UUID;

public interface PlatformNoticeGateway {
    NoticeResponse createNotice(CreateNoticeRequest request);
    NoticeResponse getNotice(UUID noticeId);
    List<NoticeResponse> getAllNotices();
    void updateNotice(UUID noticeId, UpdateNoticeRequest request);
    void deleteNotice(UUID noticeId);
}
