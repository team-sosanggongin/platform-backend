package com.platform.sosangongin.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.sosangongin.api.advices.CommonResultResponseAdvice;
import com.platform.sosangongin.api.controllers.dto.InviteApiRequest;
import com.platform.sosangongin.cases.invitation.InviteRequest;
import com.platform.sosangongin.cases.invitation.InviteResult;
import com.platform.sosangongin.cases.invitation.UserInvitationUsecase;
import com.platform.sosangongin.config.SecurityConfig;
import com.platform.sosangongin.config.WebMvcConfig;
import com.platform.sosangongin.services.jwt.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InvitationController.class)
@Import({SecurityConfig.class, WebMvcConfig.class, CommonResultResponseAdvice.class})
class InvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserInvitationUsecase userInvitationUsecase;

    @MockBean
    private JwtService jwtService; // SecurityConfig 의존성

    @Test
    @DisplayName("POST /api/v1/invitations - 성공")
    void inviteUser() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, null);

        InviteApiRequest request = new InviteApiRequest();
        request.setBranchId(UUID.randomUUID());
        request.setTargetUserPhoneNumber("010-1234-5678");
        request.setRoleIds(List.of(1L, 2L));

        InviteResult expectedResult = InviteResult.builder()
                .httpStatus(HttpStatus.OK)
                .build();

        given(userInvitationUsecase.inviteAlreadySingedUpUser(any(InviteRequest.class))).willReturn(expectedResult);

        // when & then
        mockMvc.perform(post("/api/v1/invitations")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/invitations - 실패 (권한 없음, 400)")
    void inviteUser_Fail_BadRequest() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, null);

        InviteApiRequest request = new InviteApiRequest();
        request.setBranchId(UUID.randomUUID());
        request.setTargetUserPhoneNumber("010-1234-5678");
        request.setRoleIds(List.of(1L));

        InviteResult expectedResult = InviteResult.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("invitation only can be made by the owner")
                .build();

        given(userInvitationUsecase.inviteAlreadySingedUpUser(any(InviteRequest.class))).willReturn(expectedResult);

        // when & then
        mockMvc.perform(post("/api/v1/invitations")
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("invitation only can be made by the owner"));
    }
}
