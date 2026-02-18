package com.example.GoldenReport.FilterChain;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final String token;

    public JwtAuthenticationToken(String token) {
        super((Collection<? extends GrantedAuthority>) null);
        this.principal = null;
        this.token = token;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(Object principal,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = null;
        setAuthenticated(true);
    }


    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    public @Nullable Object getToken() {
        return token;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return principal;
    }
}
