package com.example.GoldenReport.Service.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JWTProvider {
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;
    private SecretKey key;

    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    public String generateJWT(String userId, JWTType jwtType) {
        Claims claims = Jwts.claims().add("userId", userId).build();
        Date now = new Date();

        return Jwts.builder()
                .subject(userId)
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtType.getValidTime()))
                .signWith(key)
                .compact();

    }

    public Optional<String> decodeJWT(String jwt) {
        String subject = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload().getSubject();

        return Optional.ofNullable(subject);
    }
}
