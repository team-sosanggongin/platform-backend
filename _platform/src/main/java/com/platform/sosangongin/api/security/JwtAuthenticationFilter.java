package com.platform.sosangongin.api.security;

import com.platform.sosangongin.services.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            // 토큰이 없는 경우 다음 필터로 바로 이동 (인증이 필요한 경로는 SecurityConfig에서 막힘)
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            UUID userId = jwtService.getUserIdFromToken(token);

            // SecurityContext에 이미 인증 정보가 없는 경우에만 새로 생성
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // Spring Security가 이해할 수 있는 Authentication 객체 생성
                // Principal에는 userId를, Credentials에는 null, Authorities는 비워둠 (역할 기반이 필요하면 추가)
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("Authenticated user with id: {}", userId);
            }
        } catch (Exception e) {
            log.warn("Invalid JWT token processing: {}", e.getMessage());
            // 예외가 발생해도 필터 체인은 계속 진행되어야 함.
            // 인증이 필요한 경로라면 SecurityConfig의 ExceptionHandling에서 처리됨.
        }

        filterChain.doFilter(request, response);
    }
}
