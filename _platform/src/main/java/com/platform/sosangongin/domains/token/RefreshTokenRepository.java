package com.platform.sosangongin.domains.token;

import com.platform.sosangongin.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteAllByUser(User user);
    Optional<RefreshToken> findTopByUserOrderByExpiresAtDesc(User user);
}
