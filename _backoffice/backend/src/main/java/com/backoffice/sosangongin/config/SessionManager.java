package com.backoffice.sosangongin.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SessionManager {

    private static final String ACCOUNT_ID_KEY = "ACCOUNT_ID";

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

    public void invalidate(HttpSession session) {
        session.invalidate();
    }
}