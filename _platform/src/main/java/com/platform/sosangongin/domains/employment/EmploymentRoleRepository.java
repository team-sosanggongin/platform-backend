package com.platform.sosangongin.domains.employment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmploymentRoleRepository extends JpaRepository<EmploymentRole, Long> {
    List<EmploymentRole> findByEmployment(Employment employment);
}
