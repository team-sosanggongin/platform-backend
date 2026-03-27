package com.platform.sosangongin.domains.app.version;

import com.platform.sosangongin.services.version.VersionCheckUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VersionSpan {

    @Column(name = "version_code", nullable = false)
    private String versionCode;

    public boolean isCompatible(String currentAppVer) {
        return VersionCheckUtils.isVersionInSpan(this.versionCode,currentAppVer);
    }
}
