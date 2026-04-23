package com.platform.sosangongin.domains.employment;

import com.platform.sosangongin.domains.business.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    Optional<Employment> findByIdAndBusiness(Long id, Business business);
    Page<Employment> findByBusinessAndStatus(Business business, EmploymentStatus status, Pageable pageable);
    Page<Employment> findByBusiness(Business business, Pageable pageable);
}
