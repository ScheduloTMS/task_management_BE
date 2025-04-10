package com.taskmanagement.task.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class JwtUtil {

    private SecretKey SECRET_KEY;

    private final Set<String> tokenBlacklist = new HashSet<>();

    @Value("${jwt.secret.key}")
    public void setSecretKey(String secret) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }


    public String generateToken(String role, String email, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SECRET_KEY)
                .compact();
    }



    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }



    public Boolean validateToken(String token, String email) {
        final String extractedEmail = extractUsername(token);
        if (isTokenBlacklisted(token)) {
            return false;
        }
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    public void blacklistToken(String token) {
        tokenBlacklist.add(token);
    }

    private boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
