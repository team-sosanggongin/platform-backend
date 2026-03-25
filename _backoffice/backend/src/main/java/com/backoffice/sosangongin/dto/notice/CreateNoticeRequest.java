package com.backoffice.sosangongin.dto.notice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateNoticeRequest {
    private String title;
    private String content;
}
