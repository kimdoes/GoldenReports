package com.example.GoldenReport.FilterChain;

import com.example.GoldenReport.Service.JWT.JWTProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;

    private final AuthenticationFailureHandler failureHandler;

    private final AuthenticationManager authenticationManager;

    public JwtAuthorizationFilter(AuthenticationFailureHandler failureHandler,
                                  JWTProvider jwtProvider,
                                  AuthenticationManager authenticationManager) {
        this.failureHandler = failureHandler;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new JwtAuthenticationProvider(jwtProvider);
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println(path);

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = Arrays.stream(cookies)
                .filter(cookie -> "Authentication".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = new JwtAuthenticationToken(accessToken);

        try {
            Authentication returnedAuthentication = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(returnedAuthentication);
            filterChain.doFilter(request, response);

        } catch (AuthenticationException e) {
            doFailure(request, response, e);
        }
    }

    /*
    private void doSuccess(HttpServletRequest request,
                           HttpServletResponse response,
                           Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        publisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                authentication,
                this.getClass()
        ));

        successHandler.onAuthenticationSuccess(request, response, authentication);

    }
    */

    private void doFailure(HttpServletRequest request,
                           HttpServletResponse response,
                           AuthenticationException authException) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, authException);
    }
}