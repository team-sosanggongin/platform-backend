package com.backoffice.sosangongin.auth.session;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final SessionManager sessionManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session != null) {
            sessionManager.getAccountId(session).ifPresent(accountId -> {
                String role = sessionManager.getRole(session);
                setAuthentication(accountId, role);
            });
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(UUID accountId, String role) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        accountId,
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}