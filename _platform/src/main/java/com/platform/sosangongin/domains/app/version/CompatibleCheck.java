package com.platform.sosangongin.domains.app.version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class CompatibleCheck {
    private final boolean isOk;
    private final String reason;

    public CompatibleCheck(boolean isOk) {
        this.isOk = isOk;
        this.reason = "";
    }
}
