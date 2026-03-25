package com.backoffice.sosangongin.controller;

import com.backoffice.sosangongin.domains.notice.NoticeService;
import com.backoffice.sosangongin.dto.notice.CreateNoticeRequest;
import com.backoffice.sosangongin.dto.notice.NoticeResponse;
import com.backoffice.sosangongin.dto.notice.UpdateNoticeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<NoticeResponse> createNotice(@RequestBody CreateNoticeRequest request) {
        NoticeResponse response = noticeService.createNotice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> getNotice(@PathVariable UUID id) {
        NoticeResponse response = noticeService.getNotice(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponse>> getAllNotices() {
        List<NoticeResponse> responses = noticeService.getAllNotices();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNotice(@PathVariable UUID id, @RequestBody UpdateNoticeRequest request) {
        noticeService.updateNotice(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable UUID id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}
