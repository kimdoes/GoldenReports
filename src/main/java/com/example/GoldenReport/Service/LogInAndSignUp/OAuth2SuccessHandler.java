package com.example.GoldenReport.Service.LogInAndSignUp;

import com.example.GoldenReport.Domain.Member;
import com.example.GoldenReport.Repository.MemberRepository;
import com.example.GoldenReport.Service.JWT.JWTProvider;
import com.example.GoldenReport.Service.JWT.JWTType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    MemberRepository memberRepository;
    JWTProvider jwtProvider;

    public OAuth2SuccessHandler(MemberRepository memberRepository,
                                JWTProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> oAuth2UserResponse = oAuth2User.getAttribute("response");
        String userId = oAuth2UserResponse.get("id").toString();

        Optional<Member> optionalMember = memberRepository.findById(userId);

        if (optionalMember.isPresent()) {
            try {
                Member member = optionalMember.get();

                Map<String, Object> claim = new HashMap<>();
                claim.put("position", member.getPosition());
                System.out.println(member.getPosition());

                String jwtToken = jwtProvider.generateJWT(userId,
                        JWTType.ACCESS.getValidTime(),
                        List.of(claim));

                String refreshToken = jwtProvider.generateJWT(userId,
                        JWTType.REFRESH.getValidTime(),
                        List.of(claim));

                Cookie cookie = new Cookie("Authentication", jwtToken);
                cookie.setMaxAge(JWTType.ACCESS.getValidTime());
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setSecure(true);

                System.out.println("login Success!: " + jwtToken);

                Cookie cookie2 = new Cookie("Authentication_refreshToken", refreshToken);
                cookie2.setMaxAge(JWTType.REFRESH.getValidTime());
                cookie2.setPath("/");
                cookie2.setHttpOnly(true);
                cookie2.setSecure(true);

                response.addCookie(cookie);
                response.addCookie(cookie2);

                System.out.println("Token is Ready" + jwtToken);

                response.sendRedirect("/");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                System.out.println("There is No Member with userId: " + userId);

                Cookie cookie = new Cookie("Authentication_Signup", jwtProvider.generateJWT(userId, 300));
                cookie.setMaxAge(300);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setSecure(true);

                System.out.println(cookie.getValue());

                response.addCookie(cookie);
                response.setStatus(HttpServletResponse.SC_FOUND);
                response.sendRedirect("/signup");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}