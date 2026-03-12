package com.platform.sosangongin.domains.user;

import com.platform.sosangongin.domains.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteAllByUser(User user);
}
