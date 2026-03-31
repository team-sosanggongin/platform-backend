package com.platform.sosangongin.domains.app.version;

import com.platform.sosangongin.domains.common.BaseEntity;
import com.platform.sosangongin.domains.common.ClientPlatform;
import com.platform.sosangongin.services.version.VersionCheckUtils;
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
    @Column(name = "platform_type", nullable = false)
    private ClientPlatform platform;

    @Embedded
    private VersionSpan versionSpan;
    /**
     * @apiNote 치명적인 버그등으로 인하여, 클라이언트 앱을 반드시 업데이트시켜야 하는 경우, 해당 값은 true
     * **/
    @Column(name = "force_update", nullable = false)
    private boolean forceUpdate = false;

    public CompatibleCheck check(ClientPlatform platformType, String currentAppVer) throws IllegalArgumentException{
        if(!this.platform.equals(platformType)){
            return CompatibleCheck.builder()
                    .isOk(false)
                    .reason("platform is illegal")
                    .build();
        }
        if(this.versionSpan.isCompatible(currentAppVer)){
            return new CompatibleCheck(true);
        }
        return CompatibleCheck.builder()
                .isOk(false)
                .reason("current version is not supported.")
                .build();
    }

}
