package com.platform.sosangongin.services.jwt;

import com.platform.sosangongin.domains.role.BusinessRole;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.UUID;

public interface JwtService {
    String createToken(UUID userId);
    String createToken(UUID userId, UUID businessId, List<BusinessRole> roles);
    String createRefreshToken(UUID userId);
    Claims parseClaims(String token);
    UUID getUserIdFromToken(String token);
}
