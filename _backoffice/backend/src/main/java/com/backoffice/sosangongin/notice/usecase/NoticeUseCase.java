package com.backoffice.sosangongin.notice.usecase;

import com.backoffice.sosangongin.global.response.PageResponse;
import com.backoffice.sosangongin.notice.dto.*;
import com.backoffice.sosangongin.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NoticeUseCase {

    private final NoticeService noticeService;

    public NoticeResponse create(NoticeCreateRequest request, UUID createdBy, String authorName) {
        return NoticeResponse.from(noticeService.create(request, createdBy, authorName));
    }

    public List<NoticeResponse> findAll() {
        return noticeService.findAll().stream()
                .map(NoticeResponse::from)
                .toList();
    }

    public PageResponse<NoticeResponse> findAll(String keyword, Pageable pageable) {
        return PageResponse.from(
                noticeService.findAll(keyword, pageable).map(NoticeResponse::from)
        );
    }

    public NoticeResponse findById(Long id) {
        return NoticeResponse.from(noticeService.findById(id));
    }

    public NoticeResponse update(Long id, NoticeUpdateRequest request) {
        return NoticeResponse.from(noticeService.update(id, request));
    }

    public void delete(Long id) {
        noticeService.delete(id);
    }

    public void changeStatus(Long id, NoticeStatusRequest request) {
        noticeService.changeStatus(id, request.getStatus());
    }
}