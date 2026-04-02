package com.backoffice.sosangongin.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SessionManager {

    private static final String ACCOUNT_ID_KEY = "ACCOUNT_ID";
    private static final String ROLE_KEY = "ROLE";

    public static final String ROLE_ROOT = "ROLE_ROOT";
    public static final String ROLE_BACKOFFICE_USER = "ROLE_BACKOFFICE_USER";

    public void setAccountId(HttpSession session, UUID accountId) {
        session.setAttribute(ACCOUNT_ID_KEY, accountId);
    }

    public Optional<UUID> getAccountId(HttpSession session) {
        Object value = session.getAttribute(ACCOUNT_ID_KEY);
        if (value instanceof UUID accountId) {
            return Optional.of(accountId);
        }
        return Optional.empty();
    }

    public void setRole(HttpSession session, String role) {
        session.setAttribute(ROLE_KEY, role);
    }

    public String getRole(HttpSession session) {
        Object value = session.getAttribute(ROLE_KEY);
        if (value instanceof String role) {
            return role;
        }
        return ROLE_BACKOFFICE_USER;
    }

    public void invalidate(HttpSession session) {
        session.invalidate();
    }
}