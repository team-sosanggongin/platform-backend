package com.platform.sosangongin.domains.consent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsentPolicyRepository extends JpaRepository<ConsentPolicy, Long> {
    List<ConsentPolicy> findByIsActiveTrue();
}
