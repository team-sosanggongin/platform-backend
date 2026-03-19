package com.platform.sosangongin.services.jwt;

import com.platform.sosangongin.domains.role.Role;
import com.platform.sosangongin.errors.InvalidTokenException;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.UUID;

public interface JwtService {
    String createToken(UUID userId);
    String createToken(UUID userId, UUID businessId, List<Role> roles);
    String createRefreshToken(UUID userId);
    Claims parseClaims(String token);
    UUID getUserIdFromToken(String token) throws InvalidTokenException;
}
