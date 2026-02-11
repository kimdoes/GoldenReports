package com.example.GoldenReport.Service.JWT;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JWTFilter {
    JWTProvider jwtProvider;
    public JWTFilter(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public String generateAccessToken(String userId) {
        return jwtProvider.generateJWT(userId, JWTType.ACCESS);
    }

    public String generateRefreshToken(String userId) {
        return jwtProvider.generateJWT(userId, JWTType.REFRESH);
    }

    public Optional<String> getPlainString(String token) {
        return jwtProvider.decodeJWT(token);
    }
}
