package com.backoffice.sosangongin.domains.notice;

import com.backoffice.sosangongin.dto.notice.CreateNoticeRequest;
import com.backoffice.sosangongin.dto.notice.NoticeResponse;
import com.backoffice.sosangongin.dto.notice.UpdateNoticeRequest;
import com.backoffice.sosangongin.gateways.PlatformNoticeGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final PlatformNoticeGateway noticeGateway;

    public NoticeResponse createNotice(CreateNoticeRequest request) {
        return noticeGateway.createNotice(request);
    }

    public NoticeResponse getNotice(UUID noticeId) {
        return noticeGateway.getNotice(noticeId);
    }

    public List<NoticeResponse> getAllNotices() {
        return noticeGateway.getAllNotices();
    }

    public void updateNotice(UUID noticeId, UpdateNoticeRequest request) {
        noticeGateway.updateNotice(noticeId, request);
    }

    public void deleteNotice(UUID noticeId) {
        noticeGateway.deleteNotice(noticeId);
    }
}
