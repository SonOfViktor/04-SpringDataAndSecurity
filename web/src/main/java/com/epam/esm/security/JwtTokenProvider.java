package com.epam.esm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    public static final String ROLE_KEY = "role";
    private static final String BEARER = "Bearer";

    @Value("${module4.jwt.secret}")
    private String secretKey;

    @Value("${module4.jwt.header}")
    private String authorizationHeader;

    @Value("${module4.jwt.expiration-in-seconds}")
    private int validityInSeconds;

    public String createToken(String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(ROLE_KEY, role);

        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(validityInSeconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(getKey())
                .compact();
    }

    public boolean validateToken(String token) {
        boolean isTokenValid = true;

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException e) {
            isTokenValid = false;
        }

        return isTokenValid;
    }

    public Optional<String> resolveToken(HttpServletRequest request) {
        String header = request.getHeader(authorizationHeader);

        return (header != null && header.startsWith(BEARER)) ?
                Optional.of(header.replace(BEARER, "").trim()) :
                Optional.empty();
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(ROLE_KEY, String.class);
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
