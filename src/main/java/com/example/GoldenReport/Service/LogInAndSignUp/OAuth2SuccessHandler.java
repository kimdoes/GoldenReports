package com.example.GoldenReport.Service.LogInAndSignUp;

import com.example.GoldenReport.Domain.Member;
import com.example.GoldenReport.Repository.MemberRepository;
import com.example.GoldenReport.Service.JWT.JWTFilter;
import com.example.GoldenReport.Service.JWT.JWTType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    MemberRepository memberRepository;
    JWTFilter jwtFilter;
    RequestCache requestCache = new HttpSessionRequestCache();

    public OAuth2SuccessHandler(MemberRepository memberRepository,
                                JWTFilter jwtFilter) {
        this.memberRepository = memberRepository;
        this.jwtFilter = jwtFilter;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> oAuth2UserResponse = oAuth2User.getAttribute("response");
        String userId = oAuth2UserResponse.get("id").toString();

        Optional<Member> member = memberRepository.findById(userId);

        if (member.isPresent()) {
            try {
                String jwtToken = jwtFilter.generateAccessToken(userId);

                Cookie cookie = new Cookie("Token", jwtToken);
                cookie.setMaxAge((int) JWTType.ACCESS.getValidTime());
                cookie.setPath("/");
                response.addCookie(cookie);

                System.out.println("Token is Ready" + jwtToken);

                SavedRequest cache = requestCache.getRequest(request, response);

                if (cache != null) {
                    String returnURL = cache.getRedirectUrl();

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.sendRedirect(returnURL);
                } else {
                    response.setStatus(HttpServletResponse.SC_FOUND);
                    response.sendRedirect("/");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                System.out.println("There is No Member with userId: " + userId);

                response.setStatus(HttpServletResponse.SC_FOUND);
                response.sendRedirect("/signup");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}