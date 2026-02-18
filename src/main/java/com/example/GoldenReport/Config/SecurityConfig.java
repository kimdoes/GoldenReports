package com.example.GoldenReport.Config;

import com.example.GoldenReport.FilterChain.JwtAuthenticationProvider;
import com.example.GoldenReport.FilterChain.JwtAuthorizationFilter;
import com.example.GoldenReport.Repository.MemberRepository;
import com.example.GoldenReport.Service.JWT.JWTProvider;
import com.example.GoldenReport.FilterChain.JwtAuthorizationEntryPoint;
import com.example.GoldenReport.Service.LogInAndSignUp.OAuth2SuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    private final OAuth2SuccessHandler oauth2SuccessHandler;

    private final JWTProvider jwtProvider;

    public SecurityConfig(OAuth2SuccessHandler oauth2SuccessHandler,
                          JWTProvider jwtProvider) {
        this.oauth2SuccessHandler = oauth2SuccessHandler;
        this.jwtProvider = jwtProvider;
    }

    @Bean
    public JwtAuthenticationProvider JwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtProvider);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(
            JwtAuthenticationProvider authenticationProvider
    ) throws Exception {
        return new ProviderManager(Arrays.asList(authenticationProvider));
    }

    @Bean
    public AuthenticationProvider authenticationProviderBean() throws Exception {
        return new JwtAuthenticationProvider(jwtProvider);
    }

    @Bean
    public AuthenticationFailureHandler failureHandler(){
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request,
                                                HttpServletResponse response,
                                                AuthenticationException exception) throws IOException, ServletException {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        };
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(
            JWTProvider jwtProvider,
            AuthenticationFailureHandler failureHandler,
            AuthenticationManager authenticationManager
    ){
        return new JwtAuthorizationFilter(failureHandler, jwtProvider, authenticationManager);
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http,
                                            JwtAuthorizationFilter jwtAuthorizationFilter) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico/**",
                                "/h2-console/**",
                                "/signup/**",
                                "/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/review/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/")
                        .successHandler(oauth2SuccessHandler)
                        .failureUrl("/login?error=true")
                )
                .addFilterBefore(jwtAuthorizationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new JwtAuthorizationEntryPoint()))
                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}