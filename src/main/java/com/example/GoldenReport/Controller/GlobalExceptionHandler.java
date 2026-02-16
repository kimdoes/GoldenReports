package com.example.GoldenReport.Controller;

import com.example.GoldenReport.Domain.RefreshToken;
import com.example.GoldenReport.Repository.RefreshTokenRepository;
import com.example.GoldenReport.Service.JWT.JWTFilter;
import com.nimbusds.jwt.proc.ExpiredJWTException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

/**
 * AccessToken의 만료
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    RequestCache requestCache = new HttpSessionRequestCache();
    RefreshTokenRepository refreshTokenRepository;
    JWTFilter jwtFilter;

    GlobalExceptionHandler(RefreshTokenRepository refreshTokenRepository,
                           JWTFilter jwtFilter) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtFilter = jwtFilter;
    }

    @ExceptionHandler(ExpiredJWTException.class)
    public ResponseEntity<?> handleException(ExpiredJWTException ex, NativeWebRequest nativeRequest) {
        try {
            HttpServletRequest request = nativeRequest.getNativeRequest(HttpServletRequest.class);
            HttpServletResponse response = nativeRequest.getNativeResponse(HttpServletResponse.class);

            if (request == null || response == null) {
                throw new RuntimeException("request or response is null");
            }

            SavedRequest savedRequest = requestCache.getRequest(request, response);
            String refreshToken = Arrays.stream(request.getCookies())
                    .filter(cookie -> "Authentication_refreshToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);

            if (refreshToken == null) {
                throw new RuntimeException("Authentication_Refresh header not found");
            }

            Optional<String> userId = jwtFilter.getPlainString(refreshToken);

            if (userId.isEmpty()) {
                throw new RuntimeException("JWT Error");
            }

            RefreshToken token = refreshTokenRepository.findByToken(refreshToken).get(0);

            if(!token.getUserId().equals(userId.get())) {
                throw new RuntimeException("Unequalized userId between refreshToken and TokenDataBase");
            }

            Cookie cookie = new Cookie("Authentication", jwtFilter.generateAccessToken(userId.get()));
            cookie.setPath("/");

            response.addCookie(cookie);
            String redirectUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : "/";

            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();

        } catch (RuntimeException e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create("/oauth2/authorization/naver"))
                    .build();
        }
    }
}
