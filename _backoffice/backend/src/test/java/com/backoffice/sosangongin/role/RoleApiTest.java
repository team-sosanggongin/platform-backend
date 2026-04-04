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
}
