package com.example.GoldenReport.FilterChain;

import com.example.GoldenReport.Service.JWT.JWTProvider;
import io.jsonwebtoken.ExpiredJwtException;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JWTProvider jwtProvider;

    public JwtAuthenticationProvider(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    @Nullable
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String accessToken = (String) authentication.getCredentials();

            if (accessToken == null) {
                throw new BadCredentialsException("accessToken is null");
            }

            Map<String, Object> decodedToken = jwtProvider.decodeJWT(accessToken,
                    List.of("position", "subject")).orElseThrow(() -> new BadCredentialsException("accessToken is not valid"));

            String subject = Optional.ofNullable(decodedToken.get("subject"))
                    .orElseThrow(() -> new BadCredentialsException("AccessToken's subject is not Valid"))
                    .toString();

            String position = Optional.ofNullable(decodedToken.get("position"))
                    .orElseThrow(() -> new BadCredentialsException("AccessToken's position is not Valid"))
                    .toString();

            UserDetails user = new JwtUserDetails(
                    subject,
                    AuthorityUtils.createAuthorityList(position)
            );

            return new JwtAuthenticationToken(
                user, AuthorityUtils.createAuthorityList(position)
            );

        } catch (ExpiredJwtException e) {
            e.printStackTrace();
            throw new BadCredentialsException("accessToken is expired");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
