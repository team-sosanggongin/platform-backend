package com.platform.sosangongin.domains.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSocialAuthRepository extends JpaRepository<UserSocialAuth, Long> {
    UserSocialAuth findByProviderAndProviderId(SocialProvider provider, String providerId);
    List<UserSocialAuth> findByUser(User user);
}
