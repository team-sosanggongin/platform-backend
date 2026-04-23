package com.platform.sosangongin.cases.employment;

import com.platform.sosangongin.domains.employment.EmploymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResignResult {
    private final Long employmentId;
    private final String userName;
    private final EmploymentStatus status;
    private final LocalDateTime resignedAt;
}