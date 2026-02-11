package com.example.GoldenReport.Config;

import com.example.GoldenReport.Service.LogInAndSignUp.OAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    OAuth2SuccessHandler oauth2SuccessHandler;

    public SecurityConfig(OAuth2SuccessHandler oauth2SuccessHandler){
        this.oauth2SuccessHandler = oauth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/favicon.ico/**", "/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/review/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/")
                        .successHandler((request, response, authentication) -> {
                            oauth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);
                        })
                        .failureUrl("/login?error=true")
                )
                .sessionManagement((session)-> session
                        .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                )
                .requestCache((cache) -> cache
                        .requestCache(requestCache)
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                );

        return http.build();
    }
}