package com.QueensKitchenSpringBoot.controllers.Authentication.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${recipe.app.jwtSecret}") private String secret;
    @Value("${recipe.app.jwtExpirationMs}") private int ttlMillis;
    private SecretKey key() { return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder().subject(user.getUsername()).issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + ttlMillis))
            .signWith(key(), Jwts.SIG.HS512).compact();
    }
    public String getUserNameFromJwtToken(String jwt) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(jwt)
            .getPayload().getSubject();
    }
    public boolean validateJwtToken(String jwt) {
        try { Jwts.parser().verifyWith(key()).build().parseSignedClaims(jwt); return true; }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }
}
