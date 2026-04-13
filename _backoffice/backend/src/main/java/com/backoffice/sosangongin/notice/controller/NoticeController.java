package com.backoffice.sosangongin.notice.controller;

import com.backoffice.sosangongin.auth.session.SessionManager;
import com.backoffice.sosangongin.global.aop.RequiresPermission;
import com.backoffice.sosangongin.global.exception.InvalidCredentialsException;
import com.backoffice.sosangongin.global.response.PageResponse;
import com.backoffice.sosangongin.notice.dto.*;
import com.backoffice.sosangongin.notice.usecase.NoticeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeUseCase noticeUseCase;
    private final SessionManager sessionManager;

    @GetMapping
    public ResponseEntity<PageResponse<NoticeResponse>> findAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(noticeUseCase.findAll(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(noticeUseCase.findById(id));
    }

    @PostMapping
    @RequiresPermission("create.notice")
    public ResponseEntity<NoticeResponse> create(@RequestBody NoticeCreateRequest request) {
        UUID createdBy = sessionManager.getAccountId()
                .orElseThrow(() -> new InvalidCredentialsException("인증 정보가 없습니다."));
        String authorName = sessionManager.getAdminName()
                .orElseThrow(() -> new InvalidCredentialsException("인증 정보가 없습니다."));
        return ResponseEntity.created(URI.create("/api/notice"))
                .body(noticeUseCase.create(request, createdBy, authorName));
    }

    @PatchMapping("/{id}")
    @RequiresPermission("update.notice")
    public ResponseEntity<NoticeResponse> update(@PathVariable Long id,
                                                 @RequestBody NoticeUpdateRequest request) {
        return ResponseEntity.ok(noticeUseCase.update(id, request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("delete.notice")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission("update.notice")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id,
                                             @RequestBody NoticeStatusRequest request) {
        noticeUseCase.changeStatus(id, request);
        return ResponseEntity.ok().build();
    }
}
