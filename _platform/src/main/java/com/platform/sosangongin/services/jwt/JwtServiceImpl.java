package com.platform.sosangongin.services.jwt;

import com.platform.sosangongin.domains.role.Role;
import com.platform.sosangongin.domains.user.agents.UserAgent;
import com.platform.sosangongin.domains.user.agents.UserAgentDto;
import com.platform.sosangongin.errors.InvalidTokenException;
import com.platform.sosangongin.errors.InvalidTokenUsage;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    private Key getKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    @Override
    public String createToken(UUID userId, UserAgentDto userAgent) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationTime());

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("userId", userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String createToken(UUID userId, UserAgentDto userAgent, UUID businessId, List<Role> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationTime());

        List<String> roleNames = roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userId.toString()) // 표준 subject claim에 userId 사용
                .claim("userId", userId.toString()) // 명시적으로 userId claim 추가 (필요 시)
                .claim("businessId", businessId.toString())
                .claim("roles", roleNames)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String createRefreshToken(UUID userId, UserAgentDto userAgent) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshTokenExpirationTime());

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public UUID getUserIdFromToken(String token) throws InvalidTokenException{
        try {
            Claims claims = parseClaims(token);
            String userIdStr = claims.getSubject();
            if (userIdStr == null) {
                userIdStr = claims.get("userId", String.class);
            }
            return UUID.fromString(userIdStr);
        }catch (Exception e){
            throw new InvalidTokenException(e.getMessage(), token, InvalidTokenUsage.INVALID_FORMAT);
        }
    }
}
