package com.platform.sosangongin.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.sosangongin.api.advices.CommonResultResponseAdvice;
import com.platform.sosangongin.api.controllers.dto.InviteApiRequest;
import com.platform.sosangongin.cases.invitation.InviteRequest;
import com.platform.sosangongin.cases.invitation.InviteResult;
import com.platform.sosangongin.cases.invitation.UserInvitationUsecase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvitationController.class)
@Import(CommonResultResponseAdvice.class)
class InvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserInvitationUsecase userInvitationUsecase;

    @Test
    @DisplayName("POST /api/v1/invitations - 성공")
    void inviteUser() throws Exception {
        // given
        InviteApiRequest request = new InviteApiRequest();
        request.setInviterId(UUID.randomUUID());
        request.setBranchId(UUID.randomUUID());
        request.setTargetUserPhoneNumber("010-1234-5678");
        request.setRoleIds(List.of(1L, 2L));

        InviteResult expectedResult = InviteResult.builder()
                .httpStatus(HttpStatus.OK)
                .build();

        given(userInvitationUsecase.inviteAlreadySingedUpUser(any(InviteRequest.class))).willReturn(expectedResult);

        // when & then
        mockMvc.perform(post("/api/v1/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/invitations - 실패 (권한 없음, 400)")
    void inviteUser_Fail_BadRequest() throws Exception {
        // given
        InviteApiRequest request = new InviteApiRequest();
        request.setInviterId(UUID.randomUUID());
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("invitation only can be made by the owner"));
    }
}
