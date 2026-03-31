package com.backoffice.sosangongin.controller;

import com.backoffice.sosangongin.cases.notice.NoticeUsecase;
import com.backoffice.sosangongin.controller.dto.notice.CreateNoticeRequest;
import com.backoffice.sosangongin.controller.dto.notice.NoticeResponse;
import com.backoffice.sosangongin.controller.dto.notice.UpdateNoticeRequest;
import com.backoffice.sosangongin.domains.notice.BackofficeNotice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeUsecase noticeUsecase;

    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(@RequestBody CreateNoticeRequest request) {
        BackofficeNotice notice = noticeUsecase.createNotice(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(NoticeResponse.from(notice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable Long id) {
        BackofficeNotice notice = noticeUsecase.getNotice(id);
        return ResponseEntity.ok(NoticeResponse.from(notice));
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getAllNotices() {
        List<NoticeResponse> responses = noticeUsecase.getAllNotices()
                .stream()
                .map(NoticeResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponse> updateNotice(@PathVariable Long id, @RequestBody UpdateNoticeRequest request) {
        BackofficeNotice notice = noticeUsecase.updateNotice(id, request.toCommand());
        return ResponseEntity.ok(NoticeResponse.from(notice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        noticeUsecase.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}