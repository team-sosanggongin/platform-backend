package com.platform.sosangongin.api.controllers.notices;

import com.platform.sosangongin.cases.notices.PublicNoticeRequest;
import com.platform.sosangongin.cases.notices.PublicNoticeUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notice", description = "공지사항 API")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final PublicNoticeUseCase publicNoticeUseCase;

    @Operation(summary = "공지사항 목록 조회", description = "현재 유효한 공지사항을 페이징으로 조회합니다.")
    @GetMapping
    public PublicNoticeListApiResponse getNotices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new PublicNoticeListApiResponse(
                publicNoticeUseCase.getNoticeList(new PublicNoticeRequest(page, size)));
    }

    @Operation(summary = "공지사항 상세 조회", description = "공지사항 상세 내용을 조회합니다. 조회수가 증가합니다.")
    @GetMapping("/{noticeId}")
    public PublicNoticeDetailApiResponse getNoticeDetail(@PathVariable Long noticeId) {
        return new PublicNoticeDetailApiResponse(publicNoticeUseCase.getNoticeDetail(noticeId));
    }
}
