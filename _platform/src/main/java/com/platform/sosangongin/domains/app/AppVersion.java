package com.platform.sosangongin.domains.app;

import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.common.ClientPlatform;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Entity
@Table(name = "app_versions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"platform", "version_code"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppVersion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private ClientPlatform platform;

    @Column(name = "version_name", nullable = false)
    private String versionName;

    @Column(name = "version_code", nullable = false)
    private int versionCode;

    @Column(name = "min_supported_version_code", nullable = false)
    private int minSupportedVersionCode;

    @Column(name = "force_update", nullable = false)
    private boolean forceUpdate;

    public boolean isSupported(int clientVersionCode) {
        return clientVersionCode >= minSupportedVersionCode;
    }

    public boolean requiresForceUpdate(int clientVersionCode) {
        return forceUpdate && clientVersionCode < versionCode;
    }
}
