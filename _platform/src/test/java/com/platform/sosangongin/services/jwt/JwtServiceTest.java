package com.platform.sosangongin.services.jwt;

import com.platform.sosangongin.domains.role.Role;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtProperties jwtProperties;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setSecretKey("my-super-secret-key-that-is-long-enough-for-hs256");
        jwtService = new JwtServiceImpl(jwtProperties);
    }

    @Test
    @DisplayName("JWT 토큰 생성 및 파싱 성공 테스트")
    void createAndParseToken_Success() {
        // given
        UUID userId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();
        Role role = mock(Role.class);
        when(role.getRoleName()).thenReturn("ADMIN");
        List<Role> roles = Collections.singletonList(role);

        // when
        String token = jwtService.createToken(userId, new UserAgentDto(), businessId, roles);
        Claims claims = jwtService.parseClaims(token);

        // then
        assertThat(token).isNotNull();
        assertThat(claims.get("userId", String.class)).isEqualTo(userId.toString());
        assertThat(claims.get("businessId", String.class)).isEqualTo(businessId.toString());
        assertThat(claims.get("roles", List.class)).contains("ADMIN");
    }

    @Test
    @DisplayName("만료된 토큰 파싱 시 예외 발생 테스트")
    void parseExpiredToken_ThrowsException() {
        // given
        jwtProperties.setExpirationTime(-1L); // 만료 시간을 과거로 설정
        String token = jwtService.createToken(UUID.randomUUID(),new UserAgentDto(), UUID.randomUUID(), Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> jwtService.parseClaims(token))
                .isInstanceOf(ExpiredJwtException.class);
    }
}
