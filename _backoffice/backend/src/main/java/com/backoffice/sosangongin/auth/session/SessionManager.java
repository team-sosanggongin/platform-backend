package com.backoffice.sosangongin.auth.session;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Optional;
import java.util.UUID;

@Component
@RequestScope
@RequiredArgsConstructor
public class SessionManager {

    static final String ACCOUNT_ID_KEY = "ACCOUNT_ID";
    static final String ROLE_KEY = "ROLE";

    public static final String ROLE_ROOT = "ROLE_ROOT";
    public static final String ROLE_BACKOFFICE_USER = "ROLE_BACKOFFICE_USER";

    private final HttpSession session;

    public void afterLogin(UUID accountId, String role) {
        session.setAttribute(ACCOUNT_ID_KEY, accountId);
        session.setAttribute(ROLE_KEY, role);
    }

    public Optional<UUID> getAccountId() {
        Object value = session.getAttribute(ACCOUNT_ID_KEY);
        if (value instanceof UUID accountId) {
            return Optional.of(accountId);
        }
        return Optional.empty();
    }

    public String getRole() {
        Object value = session.getAttribute(ROLE_KEY);
        if (value instanceof String role) {
            return role;
        }
        return ROLE_BACKOFFICE_USER;
    }

    public void invalidate() {
        session.invalidate();
    }
}