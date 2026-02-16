package com.example.GoldenReport.Config;

import com.example.GoldenReport.Service.JWT.JWTProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;

    public JWTAuthorizationFilter(JWTProvider JWTprovider) {
        this.jwtProvider = JWTprovider;
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

        try {
            Optional<Map<String, Object>> optionalUser = jwtProvider.decodeJWT(accessToken, List.of("position"));
            if (optionalUser.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            Map<String, Object> user = optionalUser.get();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.get("subject"),
                    null,
                    AuthorityUtils.createAuthorityList(user.get("position").toString()));

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            e.printStackTrace();
            throw new BadRequestException("Invalid JWT Token", e);
        }
    }
}

/*
try {
Optional<String> optionalUserId = jwtFilter.getPlainString(httpServletRequest);

            if (optionalUserId.isEmpty()) {
        throw new NullPointerException("JWT Token is Empty");
            }

String userId = optionalUserId.get();
boolean isMember = memberRepository.existsById(userId);

            if (isMember) {
        return SearchMovie(movieSearchRequestDTO);
            } else {
                    return ResponseEntity
                    .status(HttpStatus.FOUND)
                        .location(URI.create("/signup"))
        .build();
            }

                    } catch (NullPointerException e) {
        e.printStackTrace();
            return ErrorHandler();
        }
                }

 */