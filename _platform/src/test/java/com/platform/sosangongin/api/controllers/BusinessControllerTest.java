package com.platform.sosangongin.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.sosangongin.api.advices.CommonResultResponseAdvice;
import com.platform.sosangongin.api.controllers.dto.JoinBusinessApiRequest;
import com.platform.sosangongin.cases.registration.JoinBusinessRequest;
import com.platform.sosangongin.cases.registration.JoinBusinessResult;
import com.platform.sosangongin.cases.registration.JoinBusinessUsecase;
import com.platform.sosangongin.domains.business.BusinessDto;
import com.platform.sosangongin.cases.search.SearchBusinessRequest;
import com.platform.sosangongin.cases.search.SearchBusinessResult;
import com.platform.sosangongin.cases.search.SearchBusinessUsecase;
import com.platform.sosangongin.config.SecurityConfig;
import com.platform.sosangongin.config.WebMvcConfig;
import com.platform.sosangongin.services.jwt.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BusinessController.class)
@Import({SecurityConfig.class, WebMvcConfig.class, CommonResultResponseAdvice.class})
class BusinessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SearchBusinessUsecase searchBusinessUsecase;

    @MockBean
    private JoinBusinessUsecase joinBusinessUsecase;

    @MockBean
    private JwtService jwtService; // SecurityConfig 의존성

    @Test
    @DisplayName("GET /api/v1/businesses/search - 성공")
    void searchBusinesses() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, null);

        BusinessDto dto = BusinessDto.builder()
                .businessId(UUID.randomUUID())
                .bizName("테스트 사업체")
                .build();
                
        SearchBusinessResult expectedResult = SearchBusinessResult.builder()
                .httpStatus(HttpStatus.OK)
                .businesses(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1))
                .build();
                
        given(searchBusinessUsecase.search(any(SearchBusinessRequest.class))).willReturn(expectedResult);

        // when & then
        mockMvc.perform(get("/api/v1/businesses/search")
                        .with(authentication(authentication))
                        .param("keyword", "테스트"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.businesses.content[0].bizName").value("테스트 사업체"));
    }

    @Test
    @DisplayName("POST /api/v1/businesses/{businessId}/join-requests - 성공")
    void requestToJoin() throws Exception {
        // given
        UUID businessId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId, null, null);
        
        JoinBusinessApiRequest request = new JoinBusinessApiRequest();
        
        JoinBusinessResult expectedResult = JoinBusinessResult.builder()
                .httpStatus(HttpStatus.OK)
                .message("Successfully requested to join business")
                .build();
                
        given(joinBusinessUsecase.join(any(JoinBusinessRequest.class))).willReturn(expectedResult);

        // when & then
        mockMvc.perform(post("/api/v1/businesses/{businessId}/join-requests", businessId)
                        .with(authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully requested to join business"));
    }
}
