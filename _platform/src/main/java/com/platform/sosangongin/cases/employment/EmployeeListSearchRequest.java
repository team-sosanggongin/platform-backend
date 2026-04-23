package com.platform.sosangongin.cases.employment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeListSearchRequest {
    private final String status;
    private final int page;
    private final int size;
}