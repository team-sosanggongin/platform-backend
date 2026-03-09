package com.platform.sosangongin.domains.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {
}
