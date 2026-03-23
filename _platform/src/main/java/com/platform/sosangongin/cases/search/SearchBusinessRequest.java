package com.platform.sosangongin.cases.search;

import lombok.*;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class SearchBusinessRequest {
    private final String keyword;
    private final int page;
    private final int size;

    public SearchBusinessRequest(String keyword) {
        this.keyword = keyword;
        this.page = 0;
        this.size = 10;
    }
}
