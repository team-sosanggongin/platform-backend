package com.platform.sosangongin.domains.app.version;

import com.platform.sosangongin.domains.common.ClientPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppVersionRepository extends JpaRepository<AppVersion, UUID> {

    Optional<AppVersion> findTopByPlatformOrderByCreatedAtDesc(ClientPlatform platform);

}
