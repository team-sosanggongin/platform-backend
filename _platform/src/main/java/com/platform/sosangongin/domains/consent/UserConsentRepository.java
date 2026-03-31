package com.platform.sosangongin.domains.consent;

import com.platform.sosangongin.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserConsentRepository extends JpaRepository<UserConsent, Long> {
    List<UserConsent> findByUser(User user);
    Optional<UserConsent> findByUserAndConsentPolicy(User user, ConsentPolicy consentPolicy);
}
