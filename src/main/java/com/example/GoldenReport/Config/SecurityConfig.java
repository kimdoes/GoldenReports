package com.example.GoldenReport.Config;

import com.example.GoldenReport.Repository.MemberRepository;
import com.example.GoldenReport.Service.JWT.JWTFilter;
import com.example.GoldenReport.Service.JWT.JWTProvider;
import com.example.GoldenReport.Service.LogInAndSignUp.JWTAuthorizationEntryPoint;
import com.example.GoldenReport.Service.LogInAndSignUp.OAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    OAuth2SuccessHandler oauth2SuccessHandler;
    JWTProvider jwtProvider;
    MemberRepository memberRepository;

    public SecurityConfig(OAuth2SuccessHandler oauth2SuccessHandler,
                          JWTProvider jwtProvider,
                          MemberRepository memberRepository) {
        this.oauth2SuccessHandler = oauth2SuccessHandler;
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
    }

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {

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
                        .successHandler((request, response, authentication) -> {
                            oauth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);
                        })
                        .failureUrl("/login?error=true")
                )
                .addFilterBefore(new JWTAuthorizationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new JWTAuthorizationEntryPoint()))
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