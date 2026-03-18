package com.platform.sosangongin.domains.business;

import com.platform.sosangongin.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessJoinRequestRepository extends JpaRepository<BusinessJoinRequest, Long> {
    Optional<BusinessJoinRequest> findByUserAndBusinessAndStatus(User user, Business business, BusinessJoinRequestStatus status);
    boolean existsByUserAndBusinessAndStatusIn(User user, Business business, List<BusinessJoinRequestStatus> statuses);
}
