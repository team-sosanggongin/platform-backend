package com.platform.sosangongin.cases.employment;

import com.platform.sosangongin.domains.employment.EmployeeVo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class EmployeeListResult {
    private final Page<EmployeeVo> employees;
}