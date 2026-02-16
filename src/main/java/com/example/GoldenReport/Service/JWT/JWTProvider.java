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
import java.util.*;

@Component
public class JWTProvider {
    @Value("${jwt.secret-key}")
    private String jwtSecretKey;
    private SecretKey key;

    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

    /**
     *
     * @param userId    유저 식별자
     * @param expiration  유효시간 (단위: 초)
     * @return JWT 토큰
     */
    public String generateJWT(String userId, int expiration) {
        Claims claims = Jwts.claims().build();
        expiration = expiration * 1000;
        Date now = new Date();

        return Jwts.builder()
                .subject(userId)
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    public String generateJWT(String userId,
                              int expiration,
                              Collection<Map<String, Object>> claimsContent) {
        Map<String, Object> claims = new HashMap<>();

        for (Map<String, Object> claim : claimsContent) {
            claims.putAll(claim);
        }

        expiration = expiration * 1000;
        Date now = new Date();

        return Jwts.builder()
                .subject(userId)
                .claims(claims)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    public Optional<String> decodeJWT(String jwt) {
        return Optional.ofNullable(Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload().getSubject());
    }

    public Optional<Map<String, Object>> decodeJWT(String jwt, List<String> keys) {
        Map<String, Object> results = new HashMap<>();
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt).getPayload();
        results.put("subject", claims.getSubject());

        for (String key : keys) {
            if (claims.containsKey(key)) {
                results.put(key, claims.get(key));
            }
        }

        return Optional.of(results);
    }
}
