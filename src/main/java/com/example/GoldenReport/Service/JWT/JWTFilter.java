package com.example.GoldenReport.Service.JWT;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class JWTFilter {
    JWTProvider jwtProvider;

    public JWTFilter(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public String generateAccessToken(String userId, int validTime){
        return jwtProvider.generateJWT(userId, validTime);
    }

    public String generateAccessToken(String userId) {
        return jwtProvider.generateJWT(userId, JWTType.ACCESS.getValidTime());
    }

    public String generateRefreshToken(String userId) {
        return jwtProvider.generateJWT(userId, JWTType.REFRESH.getValidTime());
    }

    public Optional<String> getPlainString(String token) {
        return jwtProvider.decodeJWT(token);
    }

    public Optional<String> getPlainString(HttpServletRequest request) {
        String accessToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "Authentication".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (accessToken == null) {
            throw new NullPointerException("Access token is null");
        }

        return getPlainString(accessToken);
    }
}
