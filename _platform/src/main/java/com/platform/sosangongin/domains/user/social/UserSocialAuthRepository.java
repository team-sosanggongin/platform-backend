package com.platform.sosangongin.domains.user.social;

import com.platform.sosangongin.domains.user.SocialProvider;
import com.platform.sosangongin.domains.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSocialAuthRepository extends JpaRepository<UserSocialAuth, Long> {
    UserSocialAuth findByProviderAndProviderUserId(SocialProvider provider, String providerUserId);
    List<UserSocialAuth> findByUser(User user);
}
