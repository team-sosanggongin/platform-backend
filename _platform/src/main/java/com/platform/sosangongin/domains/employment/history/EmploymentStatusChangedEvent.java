package com.platform.sosangongin.domains.employment.history;

import com.platform.sosangongin.domains.employment.EmploymentStatus;

public record EmploymentStatusChangedEvent(
        Long employmentId,
        EmploymentStatus previousStatus,
        EmploymentStatus newStatus
) {
}
