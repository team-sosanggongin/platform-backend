package com.platform.sosangongin.cases.role;

import com.platform.sosangongin.domains.role.*;
import com.platform.sosangongin.domains.role.permission.Permission;
import com.platform.sosangongin.domains.role.permission.PermissionDomain;
import com.platform.sosangongin.domains.role.permission.PermissionRepository;
import com.platform.sosangongin.domains.user.User;
import com.platform.sosangongin.domains.user.UserRepository;
import com.platform.sosangongin.errors.AccessDeniedException;
import com.platform.sosangongin.errors.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoleUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleUseCase roleUseCase;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .name("테스트유저")
                .phoneNumber("010-1234-5678")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);
    }

    // ── 목록 조회 ──

    @Test
    @DisplayName("내가 생성한 역할 목록 조회: 역할이 있는 경우 목록 반환")
    void getMyRoles_HasRoles() {
        // given
        Role role1 = Role.builder()
                .id(1L).roleName("매니저").description("매장 관리 역할")
                .isRecommended(true).platformType(PlatformType.PLATFORM).isActive(true).createdBy(user)
                .build();

        Role role2 = Role.builder()
                .id(2L).roleName("알바생").description("파트타임 직원")
                .isRecommended(false).platformType(PlatformType.PLATFORM).isActive(true).createdBy(user)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.findByCreatedByAndPlatformType(user, PlatformType.PLATFORM)).willReturn(List.of(role1, role2));

        // when
        RoleListResult result = roleUseCase.getMyRoles(userId.toString());

        // then
        assertThat(result.getRoles()).hasSize(2);
        assertThat(result.getRoles().get(0).getRoleName()).isEqualTo("매니저");
        assertThat(result.getRoles().get(0).isRecommended()).isTrue();
        assertThat(result.getRoles().get(1).getRoleName()).isEqualTo("알바생");
    }

    @Test
    @DisplayName("내가 생성한 역할 목록 조회: 역할이 없는 경우 빈 목록 반환")
    void getMyRoles_Empty() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.findByCreatedByAndPlatformType(user, PlatformType.PLATFORM)).willReturn(List.of());

        // when
        RoleListResult result = roleUseCase.getMyRoles(userId.toString());

        // then
        assertThat(result.getRoles()).isEmpty();
    }

    @Test
    @DisplayName("내가 생성한 역할 목록 조회: 존재하지 않는 사용자인 경우 예외 발생")
    void getMyRoles_UserNotFound() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> roleUseCase.getMyRoles(userId.toString()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ── 상세 조회 ──

    @Test
    @DisplayName("역할 상세 조회: 역할과 연결된 권한 목록을 함께 반환")
    void getRoleDetail_WithPermissions() {
        // given
        Role role = Role.builder()
                .id(1L).roleName("매니저").description("매장 관리 역할")
                .isRecommended(true).platformType(PlatformType.PLATFORM).isActive(true).createdBy(user)
                .build();

        Permission perm1 = createPermission(1L, "공지 작성", "write.notice", true);
        Permission perm2 = createPermission(2L, "공지 조회", "read.notice", true);

        RolePermission rp1 = RolePermission.builder().role(role).permission(perm1).build();
        RolePermission rp2 = RolePermission.builder().role(role).permission(perm2).build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.findById(1L)).willReturn(Optional.of(role));
        given(rolePermissionRepository.findByRole(role)).willReturn(List.of(rp1, rp2));

        // when
        RoleDetailResult result = roleUseCase.getRoleDetail(userId.toString(), 1L);

        // then
        assertThat(result.getRole().getRoleName()).isEqualTo("매니저");
        assertThat(result.getPermissions()).hasSize(2);
        assertThat(result.getPermissions().get(0).getPermissionName()).isEqualTo("공지 작성");
        assertThat(result.getPermissions().get(0).getPermDomain()).isEqualTo("write.notice");
        assertThat(result.getPermissions().get(1).getPermissionName()).isEqualTo("공지 조회");
    }

    @Test
    @DisplayName("역할 상세 조회: 연결된 권한이 없는 경우 빈 권한 목록 반환")
    void getRoleDetail_NoPermissions() {
        // given
        Role role = Role.builder()
                .id(1L).roleName("신규역할").description("아직 권한 없음")
                .platformType(PlatformType.PLATFORM).isActive(true).createdBy(user)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.findById(1L)).willReturn(Optional.of(role));
        given(rolePermissionRepository.findByRole(role)).willReturn(List.of());

        // when
        RoleDetailResult result = roleUseCase.getRoleDetail(userId.toString(), 1L);

        // then
        assertThat(result.getRole().getRoleName()).isEqualTo("신규역할");
        assertThat(result.getPermissions()).isEmpty();
    }

    @Test
    @DisplayName("역할 상세 조회: 다른 사용자가 생성한 역할에 접근 시 예외 발생")
    void getRoleDetail_AccessDenied() {
        // given
        User otherUser = User.builder().name("다른유저").phoneNumber("010-9999-9999").build();
        ReflectionTestUtils.setField(otherUser, "id", UUID.randomUUID());

        Role role = Role.builder()
                .id(1L).roleName("매니저").description("매장 관리 역할")
                .platformType(PlatformType.PLATFORM).isActive(true).createdBy(otherUser)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.findById(1L)).willReturn(Optional.of(role));

        // when & then
        assertThatThrownBy(() -> roleUseCase.getRoleDetail(userId.toString(), 1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("역할 상세 조회: 존재하지 않는 역할인 경우 예외 발생")
    void getRoleDetail_RoleNotFound() {
        // given
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> roleUseCase.getRoleDetail(userId.toString(), 999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ── 활성 권한 목록 조회 ──

    @Test
    @DisplayName("활성 권한 목록 조회: permDomain 기준 오른쪽(루트)부터 정렬하여 반환")
    void getActivePermissions_SortedByPermDomain() {
        // given
        Permission perm1 = createPermission(1L, "공지 작성", "write.notices", true);
        Permission perm2 = createPermission(2L, "공지 조회", "read.notices", true);
        Permission perm3 = createPermission(3L, "공지 전체", "*.notices", true);
        Permission perm4 = createPermission(4L, "공지 루트", "notices", true);
        Permission perm5 = createPermission(5L, "댓글 조회", "read.comment", true);

        given(permissionRepository.findByPlatformTypeAndIsActiveTrueAndDeletedAtIsNull(PlatformType.PLATFORM))
                .willReturn(List.of(perm1, perm2, perm3, perm4, perm5));

        // when
        PermissionListResult result = roleUseCase.getActivePermissions();

        // then
        assertThat(result.getPermissions()).hasSize(5);
        assertThat(result.getPermissions().get(0).getPermDomain()).isEqualTo("read.comment");
        assertThat(result.getPermissions().get(1).getPermDomain()).isEqualTo("notices");
        assertThat(result.getPermissions().get(2).getPermDomain()).isEqualTo("*.notices");
        assertThat(result.getPermissions().get(3).getPermDomain()).isEqualTo("read.notices");
        assertThat(result.getPermissions().get(4).getPermDomain()).isEqualTo("write.notices");
    }

    @Test
    @DisplayName("활성 권한 목록 조회: 활성 권한이 없는 경우 빈 목록 반환")
    void getActivePermissions_Empty() {
        // given
        given(permissionRepository.findByPlatformTypeAndIsActiveTrueAndDeletedAtIsNull(PlatformType.PLATFORM))
                .willReturn(List.of());

        // when
        PermissionListResult result = roleUseCase.getActivePermissions();

        // then
        assertThat(result.getPermissions()).isEmpty();
    }

    // ── 역할 생성 ──

    @Test
    @DisplayName("역할 생성: 이름, 설명, 권한 매핑 포함하여 생성 성공")
    void createRole_Success() {
        // given
        Permission perm = createPermission(10L, "공지 작성", "write.notices", true);

        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .roleName("신규역할")
                .description("새로 만든 역할")
                .permissionIds(List.of(10L))
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.save(any(Role.class))).willAnswer(inv -> inv.getArgument(0));
        given(permissionRepository.findAllById(List.of(10L))).willReturn(List.of(perm));

        // when
        RoleDetailResult result = roleUseCase.createRole(userId.toString(), request);

        // then
        assertThat(result.getRole().getRoleName()).isEqualTo("신규역할");
        assertThat(result.getRole().getDescription()).isEqualTo("새로 만든 역할");
        assertThat(result.getPermissions()).hasSize(1);
        assertThat(result.getPermissions().get(0).getPermissionName()).isEqualTo("공지 작성");

        ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(captor.capture());
        assertThat(captor.getValue().getCreatedBy()).isEqualTo(user);
        assertThat(captor.getValue().getPlatformType()).isEqualTo(PlatformType.PLATFORM);
        assertThat(captor.getValue().isActive()).isTrue();
    }

    @Test
    @DisplayName("역할 생성: 권한 없이 생성 성공")
    void createRole_WithoutPermissions() {
        // given
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .roleName("빈역할")
                .description("권한 없음")
                .permissionIds(List.of())
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.save(any(Role.class))).willAnswer(inv -> inv.getArgument(0));
        given(permissionRepository.findAllById(List.of())).willReturn(List.of());

        // when
        RoleDetailResult result = roleUseCase.createRole(userId.toString(), request);

        // then
        assertThat(result.getRole().getRoleName()).isEqualTo("빈역할");
        assertThat(result.getPermissions()).isEmpty();
    }

    // ── 역할 수정 ──

    @Test
    @DisplayName("역할 수정: 이름, 설명, 권한 매핑 변경 성공")
    void updateRole_Success() {
        // given
        Role role = Role.builder()
                .id(1L).roleName("매니저").description("기존 설명")
                .platformType(PlatformType.PLATFORM).isActive(true).createdBy(user)
                .build();

        Permission perm1 = createPermission(10L, "공지 작성", "write.notices", true);
        Permission perm2 = createPermission(20L, "공지 조회", "read.notices", true);

        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .roleName("시니어 매니저")
                .description("변경된 설명")
                .permissionIds(List.of(10L, 20L))
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.findById(1L)).willReturn(Optional.of(role));
        given(permissionRepository.findAllById(List.of(10L, 20L))).willReturn(List.of(perm1, perm2));

        // when
        RoleDetailResult result = roleUseCase.updateRole(userId.toString(), 1L, request);

        // then
        assertThat(result.getRole().getRoleName()).isEqualTo("시니어 매니저");
        assertThat(result.getRole().getDescription()).isEqualTo("변경된 설명");
        assertThat(result.getPermissions()).hasSize(2);
        verify(rolePermissionRepository).deleteAllByRole(role);
        verify(rolePermissionRepository).saveAll(any());
    }

    @Test
    @DisplayName("역할 수정: 권한을 빈 목록으로 변경하면 기존 매핑 모두 삭제")
    void updateRole_EmptyPermissions() {
        // given
        Role role = Role.builder()
                .id(1L).roleName("매니저").description("설명")
                .platformType(PlatformType.PLATFORM).isActive(true).createdBy(user)
                .build();

        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .roleName("매니저")
                .description("설명")
                .permissionIds(List.of())
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.findById(1L)).willReturn(Optional.of(role));
        given(permissionRepository.findAllById(List.of())).willReturn(List.of());

        // when
        RoleDetailResult result = roleUseCase.updateRole(userId.toString(), 1L, request);

        // then
        assertThat(result.getPermissions()).isEmpty();
        verify(rolePermissionRepository).deleteAllByRole(role);
    }

    @Test
    @DisplayName("역할 수정: 다른 사용자의 역할 수정 시 접근 거부")
    void updateRole_AccessDenied() {
        // given
        User otherUser = User.builder().name("다른유저").phoneNumber("010-9999-9999").build();
        ReflectionTestUtils.setField(otherUser, "id", UUID.randomUUID());

        Role role = Role.builder()
                .id(1L).roleName("매니저").description("설명")
                .platformType(PlatformType.PLATFORM).isActive(true).createdBy(otherUser)
                .build();

        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .roleName("변경").description("변경").permissionIds(List.of())
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.findById(1L)).willReturn(Optional.of(role));

        // when & then
        assertThatThrownBy(() -> roleUseCase.updateRole(userId.toString(), 1L, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("역할 수정: 존재하지 않는 역할 수정 시 예외 발생")
    void updateRole_RoleNotFound() {
        // given
        UpdateRoleRequest request = UpdateRoleRequest.builder()
                .roleName("변경").description("변경").permissionIds(List.of())
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> roleUseCase.updateRole(userId.toString(), 999L, request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private Permission createPermission(Long id, String name, String domain, boolean active) {
        Permission permission = new Permission();
        ReflectionTestUtils.setField(permission, "id", id);
        ReflectionTestUtils.setField(permission, "permissionName", name);
        ReflectionTestUtils.setField(permission, "permDomain", new PermissionDomain(domain));
        ReflectionTestUtils.setField(permission, "description", name + " 권한");
        ReflectionTestUtils.setField(permission, "platformType", PlatformType.PLATFORM);
        ReflectionTestUtils.setField(permission, "isActive", active);
        return permission;
    }
}
