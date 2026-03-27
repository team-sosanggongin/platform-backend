package com.platform.sosangongin.domains.business.join;

import com.platform.sosangongin.domains.business.Business;
import com.platform.sosangongin.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessJoinRequestRepository extends JpaRepository<BusinessJoinRequest, Long> {
    Optional<BusinessJoinRequest> findByUserAndBusinessAndStatus(User user, Business business, BusinessJoinRequestStatus status);
    boolean existsByUserAndBusinessAndStatusIn(User user, Business business, List<BusinessJoinRequestStatus> statuses);
}
