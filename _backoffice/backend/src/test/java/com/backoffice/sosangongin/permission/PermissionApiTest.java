package com.backoffice.sosangongin.permission;

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

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PermissionApiTest {

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
    @DisplayName("permission 생성 → 201")
    void create_permission() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("permissionName", "test.create", "permDomain", "test"));

        mockMvc.perform(post("/api/permission")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.permissionName").value("test.create"));
    }

    @Test
    @DisplayName("permission 중복 생성 → 409")
    void create_duplicate_permission() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("permissionName", "dup.test", "permDomain", "dup"));

        mockMvc.perform(post("/api/permission")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/permission")
                        .session(rootSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("permission 전체 조회 → 200")
    void findAll_permissions() throws Exception {
        mockMvc.perform(get("/api/permission")
                        .session(rootSession))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("미인증 접근 → 401")
    void unauthenticated_access() throws Exception {
        mockMvc.perform(get("/api/permission"))
                .andExpect(status().isUnauthorized());
    }
}
