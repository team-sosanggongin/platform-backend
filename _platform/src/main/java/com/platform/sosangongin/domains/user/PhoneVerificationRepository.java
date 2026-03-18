package com.platform.sosangongin.domains.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {
    Optional<PhoneVerification> findTopByUserOrderByCreatedAtDesc(User user);
}
