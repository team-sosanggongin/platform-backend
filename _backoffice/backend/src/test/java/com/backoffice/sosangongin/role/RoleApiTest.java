package com.backoffice.sosangongin.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession rootSession;

    @BeforeEach
    void login() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("loginId", "root", "password", "root"));

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        rootSession = (MockHttpSession) result.getRequest().getSession();
    }

    @Test
    @DisplayName("role 생성 → 201")
    void create_role() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("roleName", "test-editor", "description", "테스트 편집자"));

        mockMvc.perform(post("/api/role")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roleName").value("test-editor"));
    }

    @Test
    @DisplayName("role 중복 생성 → 409")
    void create_duplicate_role() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("roleName", "dup-role", "description", "중복"));

        mockMvc.perform(post("/api/role")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/role")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("role 전체 조회 → 200")
    void findAll_roles() throws Exception {
        mockMvc.perform(get("/api/role")
                        .session(rootSession))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("role-permission 매핑 (존재하지 않는 permissionId) → 404")
    void updatePermissions_notFound() throws Exception {
        String createBody = objectMapper.writeValueAsString(
                Map.of("roleName", "perm-test-role", "description", "매핑테스트"));

        MvcResult createResult = mockMvc.perform(post("/api/role")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andReturn();

        Long roleId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("id").asLong();

        String mappingBody = objectMapper.writeValueAsString(
                Map.of("permissionIds", List.of(99999L)));

        mockMvc.perform(put("/api/role/" + roleId + "/permissions")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mappingBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("미인증 접근 → 401")
    void unauthenticated_access() throws Exception {
        mockMvc.perform(get("/api/role"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("role-permission 매핑 정상 - 신규 생성")
    void updatePermissions_createNew() throws Exception {
        Long permId1 = createPermission("reg.perm.new.one", "regtest-new");
        Long permId2 = createPermission("reg.perm.new.two", "regtest-new");
        Long roleId = createRoleForTest("regression-role-new", "신규 매핑 회귀");

        String body = objectMapper.writeValueAsString(
                Map.of("permissionIds", List.of(permId1, permId2)));

        mockMvc.perform(put("/api/role/" + roleId + "/permissions")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/role/" + roleId)
                        .session(rootSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions.length()").value(2));
    }

    @Test
    @DisplayName("role-permission 매핑 정상 - 기존 권한 유지 + 신규 추가 (NonUniqueObjectException 회귀)")
    void updatePermissions_keepExistingAndAdd() throws Exception {
        Long permId1 = createPermission("reg.perm.keep.one", "regtest-keep");
        Long permId2 = createPermission("reg.perm.keep.two", "regtest-keep");
        Long permId3 = createPermission("reg.perm.keep.three", "regtest-keep");
        Long roleId = createRoleForTest("regression-role-keep", "기존 유지 + 추가 회귀");

        String firstBody = objectMapper.writeValueAsString(
                Map.of("permissionIds", List.of(permId1, permId2)));
        mockMvc.perform(put("/api/role/" + roleId + "/permissions")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstBody))
                .andExpect(status().isOk());

        String secondBody = objectMapper.writeValueAsString(
                Map.of("permissionIds", List.of(permId1, permId2, permId3)));
        mockMvc.perform(put("/api/role/" + roleId + "/permissions")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/role/" + roleId)
                        .session(rootSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions.length()").value(3));
    }

    @Test
    @DisplayName("role-permission 매핑 정상 - 일부 제거 + 일부 추가")
    void updatePermissions_removeAndAdd() throws Exception {
        Long permId1 = createPermission("reg.perm.swap.one", "regtest-swap");
        Long permId2 = createPermission("reg.perm.swap.two", "regtest-swap");
        Long permId3 = createPermission("reg.perm.swap.three", "regtest-swap");
        Long roleId = createRoleForTest("regression-role-swap", "제거+추가 회귀");

        String firstBody = objectMapper.writeValueAsString(
                Map.of("permissionIds", List.of(permId1, permId2)));
        mockMvc.perform(put("/api/role/" + roleId + "/permissions")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstBody))
                .andExpect(status().isOk());

        String secondBody = objectMapper.writeValueAsString(
                Map.of("permissionIds", List.of(permId2, permId3)));
        mockMvc.perform(put("/api/role/" + roleId + "/permissions")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/role/" + roleId)
                        .session(rootSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions.length()").value(2));
    }

    private Long createPermission(String name, String domain) throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("permissionName", name, "permDomain", domain));
        MvcResult result = mockMvc.perform(post("/api/permission")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    private Long createRoleForTest(String name, String description) throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("roleName", name, "description", description));
        MvcResult result = mockMvc.perform(post("/api/role")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }
}
