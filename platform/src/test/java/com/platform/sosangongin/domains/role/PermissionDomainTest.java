package com.platform.sosangongin.domains.role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PermissionDomainTest {

    @ParameterizedTest
    @ValueSource(strings = {"write.notice", "read.comment", "delete.post.comment", "admin", "*.notice", "write.*"})
    @DisplayName("유효한 형식의 권한 도메인 생성 성공")
    void createPermissionDomainSuccess(String validDomain) {
        // when
        PermissionDomain permissionDomain = new PermissionDomain(validDomain);

        // then
        assertThat(permissionDomain.getValue()).isEqualTo(validDomain);
    }

    @ParameterizedTest
    @ValueSource(strings = {".write.notice", "write.notice.", "write..notice", " ", ""})
    @DisplayName("유효하지 않은 형식의 권한 도메인 생성 실패")
    void createPermissionDomainFail(String invalidDomain) {
        // when & then
        assertThatThrownBy(() -> new PermissionDomain(invalidDomain))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("하위 도메인 여부 확인 테스트")
    void isSubDomainOf() {
        // given
        PermissionDomain parent = new PermissionDomain("write");
        PermissionDomain child = new PermissionDomain("write.notice");
        PermissionDomain notChild = new PermissionDomain("read.notice");
        PermissionDomain same = new PermissionDomain("write");

        // then
        assertThat(child.isSubDomainOf(parent)).isTrue();
        assertThat(notChild.isSubDomainOf(parent)).isFalse();
        assertThat(same.isSubDomainOf(parent)).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            // child, parent, expected
            "write.notice, write, true",
            "write.notice, write.notice, true",
            "write.notice.comment, write, true",
            "write.notice, *.notice, true",
            "read.notice, *.notice, true",
            "write.notice, write.*, true",
            "write.comment, write.*, true",
            "read.notice, write, false",
            "write, write.notice, false"
    })
    @DisplayName("하위 도메인 여부 확인 테스트 (와일드카드 포함)")
    void isSubDomainOf(String childDomain, String parentDomain, boolean expected) {
        PermissionDomain child = new PermissionDomain(childDomain);
        PermissionDomain parent = new PermissionDomain(parentDomain);
        boolean result = child.isSubDomainOf(parent);

        assertThat(result).isEqualTo(expected);
    }
}
