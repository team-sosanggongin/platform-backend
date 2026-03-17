package com.platform.sosangongin.cases.notices;

import com.platform.sosangongin.domains.notices.PublicNotice;
import com.platform.sosangongin.domains.notices.PublicNoticeRepository;
import com.platform.sosangongin.services.times.TimeGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Component
public class PublicNoticeUseCase {

    private final PublicNoticeRepository noticeRepository;
    private final TimeGeneratorService timeGeneratorService;
    /**
     * 유저용 공지사항 리스트 페이징 조회
     */
    @Transactional(readOnly = true)
    public PublicNoticeResult getNoticeList(PublicNoticeRequest request) {
        // 1. 페이징 정보 설정 (0페이지부터 시작)
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        // 2. 현재 시간 기준으로 유효한 공지사항 조회
        Page<PublicNoticeVo> noticePage = noticeRepository.findActiveNotices(this.timeGeneratorService.now(), pageable)
                .map(PublicNoticeVo::from);

        // 3. DTO로 변환하여 반환
        return new PublicNoticeResult(HttpStatus.OK, "", noticePage);
    }

    /**
     * 공지사항 상세 조회 (조회수 증가 포함)
     */
    @Transactional
    public PublicNoticeDetailResult getNoticeDetail(Long noticeId) {
        Optional<PublicNotice> notice = noticeRepository.findById(noticeId);

        if(notice.isEmpty()){
            return PublicNoticeDetailResult.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message("notice is not found")
                    .build();
        }

        PublicNotice publicNotice = notice.get();
        if(!publicNotice.isDisplayable(this.timeGeneratorService.now())){
            return PublicNoticeDetailResult.builder()
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("you can`t access this notice")
                    .build();
        }

        // 조회수 증가
        publicNotice.incrementViewCount();

        return PublicNoticeDetailResult.builder()
                .vo(PublicNoticeVo.from(publicNotice))
                .httpStatus(HttpStatus.OK)
                .build();
    }
}
