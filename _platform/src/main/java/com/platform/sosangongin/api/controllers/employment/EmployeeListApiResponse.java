package com.platform.sosangongin.api.controllers.employment;

import com.platform.sosangongin.domains.employment.EmployeeVo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class EmployeeListApiResponse {
    private final Page<EmployeeVo> employees;
}