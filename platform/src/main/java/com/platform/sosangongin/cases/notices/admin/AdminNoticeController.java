package com.platform.sosangongin.cases.notices.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/admin/api/notices")
@RequiredArgsConstructor
public class AdminNoticeController {
    private final AdminNoticeManageUseCase adminNoticeManageUseCase;

    @PostMapping
    public ResponseEntity<Void> createNotice(@RequestBody AdminNoticeCreateRequest request) {
        Long noticeId = adminNoticeManageUseCase.createNotice(request);
        return ResponseEntity.created(URI.create("admin/api/notices/" + noticeId)).build();
    }
}
