package com.platform.sosangongin.domains.role;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PermissionDomain {

    // 알파벳, 숫자, 또는 '*' 허용. 각 세그먼트는 점(.)으로 구분
    // 예: "write.notice", "*.notice", "write.*"
    private static final Pattern PATTERN = Pattern.compile("^(\\*|[a-zA-Z0-9]+)(\\.(\\*|[a-zA-Z0-9]+))*$");

    private String value;

    public PermissionDomain(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Permission domain cannot be empty");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid permission domain format: " + value);
        }
    }

    /**
     * 현재 도메인(this)이 부모 도메인(parent)의 하위 도메인인지(포함되는지) 확인합니다.
     * 와일드카드(*)를 지원합니다.
     */
    public boolean isSubDomainOf(PermissionDomain parent) {
        String[] parentParts = parent.value.split("\\.");
        String[] childParts = this.value.split("\\.");

        // 부모가 자식보다 세그먼트가 많으면 포함될 수 없음 (단, 부모가 정확히 일치하는 경우는 제외)
        if (parentParts.length > childParts.length) {
            return false;
        }

        for (int i = 0; i < parentParts.length; i++) {
            String pPart = parentParts[i];
            String cPart = childParts[i];

            if (pPart.equals("*")) {
                continue;
            }

            if (!pPart.equals(cPart)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return value;
    }
}
