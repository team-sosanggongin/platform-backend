package com.platform.sosangongin.domains.employment.history;

import com.platform.sosangongin.domains.employment.Employment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmploymentStatusHistoryRepository extends JpaRepository<EmploymentStatusHistory, Long> {
    List<EmploymentStatusHistory> findByEmploymentOrderByCreatedAtDesc(Employment employment);
}
