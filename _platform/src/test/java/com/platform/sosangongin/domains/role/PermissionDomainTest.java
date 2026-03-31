package com.platform.sosangongin.domains.role;

import com.platform.sosangongin.domains.role.permission.PermissionDomain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

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
    @ValueSource(strings = {".write.notice", "write.notice.", "write..notice", " ", "", "w*ite.notice"})
    @DisplayName("유효하지 않은 형식의 권한 도메인 생성 실패")
    void createPermissionDomainFail(String invalidDomain) {
        // when & then
        assertThatThrownBy(() -> new PermissionDomain(invalidDomain))
                .isInstanceOf(IllegalArgumentException.class);
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
            "write, write.notice, false",
            "write.notice, *.comment, false"
    })
    @DisplayName("하위 도메인 여부 확인 테스트 (와일드카드 포함)")
    void isSubDomainOf(String childDomain, String parentDomain, boolean expected) {
        // given
        PermissionDomain child = new PermissionDomain(childDomain);
        PermissionDomain parent = new PermissionDomain(parentDomain);

        // when
        boolean result = child.isSubDomainOf(parent);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("정렬: 루트 도메인이 같으면 부모(짧은 것)가 먼저")
    void compareTo_ParentBeforeChild() {
        PermissionDomain parent = new PermissionDomain("notices");
        PermissionDomain child = new PermissionDomain("read.notices");

        assertThat(parent.compareTo(child)).isLessThan(0);
        assertThat(child.compareTo(parent)).isGreaterThan(0);
    }

    @Test
    @DisplayName("정렬: 와일드카드(*)가 같은 위치의 알파벳보다 앞에 위치")
    void compareTo_WildcardFirst() {
        PermissionDomain wildcard = new PermissionDomain("*.notices");
        PermissionDomain named = new PermissionDomain("read.notices");

        assertThat(wildcard.compareTo(named)).isLessThan(0);
        assertThat(named.compareTo(wildcard)).isGreaterThan(0);
    }

    @Test
    @DisplayName("정렬: 서로 다른 루트 도메인은 루트 알파벳 순")
    void compareTo_DifferentRoots() {
        PermissionDomain comment = new PermissionDomain("read.comment");
        PermissionDomain notices = new PermissionDomain("read.notices");

        assertThat(comment.compareTo(notices)).isLessThan(0);
    }

    @Test
    @DisplayName("정렬: 복합 케이스 — 오른쪽(루트)부터 비교, 와일드카드, 깊이 종합 검증")
    void compareTo_ComplexSorting() {
        List<PermissionDomain> domains = Arrays.asList(
                new PermissionDomain("read.comment.notices"),
                new PermissionDomain("*.notices"),
                new PermissionDomain("notices"),
                new PermissionDomain("read.notices"),
                new PermissionDomain("write.notices"),
                new PermissionDomain("read.comment")
        );

        List<String> sorted = domains.stream()
                .sorted()
                .map(PermissionDomain::getValue)
                .toList();

        // 루트(오른쪽)가 comment인 것 먼저, 그 다음 notices
        // 같은 루트 내에서는: 짧은 것(부모) → * → 알파벳 순
        assertThat(sorted).containsExactly(
                "read.comment",         // 루트=comment
                "notices",              // 루트=notices, 1 세그먼트 (부모)
                "*.notices",            // 루트=notices, 2 세그먼트, *
                "read.comment.notices", // 루트=notices, 3 세그먼트, 두번째=comment < read
                "read.notices",         // 루트=notices, 2 세그먼트, read
                "write.notices"         // 루트=notices, 2 세그먼트, write
        );
    }
}
