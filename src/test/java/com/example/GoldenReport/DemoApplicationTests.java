package com.example.GoldenReport;

import com.example.GoldenReport.Repository.MemberRepository;
import com.example.GoldenReport.Service.LogInAndSignUp.OAuth2SuccessHandler;
import com.example.GoldenReport.Service.LogInAndSignUp.SignupService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class DemoApplicationTests {

    @Value("${naver.userId}")
    private String naverUserId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OAuth2SuccessHandler oauth2SuccessHandler;

    @Autowired
    private MemberRepository memberRepository;

	@Test
    @DisplayName("회원가입 성공!")
	public void loginTest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        Map<String,Object> attributes = new HashMap<>();
        Map<String, Object> innerAttributes = new HashMap<>();

        innerAttributes.put("id", naverUserId);
        attributes.put("response", innerAttributes);

        OAuth2User principal = new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"), attributes, "response");

        Authentication auth = new OAuth2AuthenticationToken(
                principal, principal.getAuthorities(), "naver");

        oauth2SuccessHandler.onAuthenticationSuccess(request, response, auth);
        Cookie authCookie = response.getCookie("Authentication");

        mockMvc.perform(post("/signup")
                .cookie(authCookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"컬러잇!\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("영화검색 중 인증실패!")
    public void movieSearchTest_FailedByAuthentication() throws Exception {
        mockMvc.perform(post("/movie")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"퀸스 갬빗\", \"page\": 1}"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/oauth2/authorization/naver"));
    }

    @Test
    @DisplayName("영화검색 성공!")
    public void movieSearchTest_Success() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request2 = new MockHttpServletRequest();
        MockHttpServletResponse responseForMovie =  new MockHttpServletResponse();

        Map<String,Object> attributes = new HashMap<>();
        Map<String, Object> innerAttributes = new HashMap<>();

        innerAttributes.put("id", naverUserId);
        attributes.put("response", innerAttributes);

        OAuth2User principal = new DefaultOAuth2User(
                AuthorityUtils.createAuthorityList("ROLE_USER"), attributes, "response");

        Authentication auth = new OAuth2AuthenticationToken(
                principal, principal.getAuthorities(), "naver");

        oauth2SuccessHandler.onAuthenticationSuccess(request, response, auth);
        Cookie authCookie = response.getCookie("Authentication_Signup");

        MvcResult result = mockMvc.perform(post("/signup")
                        .cookie(authCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"컬러잇!\"}"))
                .andReturn();


        System.out.println(memberRepository.existsById(naverUserId));
        oauth2SuccessHandler.onAuthenticationSuccess(request2, responseForMovie, auth);
        Cookie accessTokenCookie = responseForMovie.getCookie("Authentication");
        Cookie refreshTokenCookie = responseForMovie.getCookie("Authentication_refreshToken");

        Assertions.assertNotNull(accessTokenCookie);
        String accessToken = accessTokenCookie.getValue();
        Assertions.assertNotNull(refreshTokenCookie);
        String refreshToken = refreshTokenCookie.getValue();
        Assertions.assertNotNull(accessToken);
        Assertions.assertNotNull(refreshToken);

        Cookie cookie = new Cookie("Authentication", accessToken);
        cookie.setPath("/");

        Cookie cookie2 = new Cookie("Authentication_refreshToken", refreshToken);
        cookie2.setPath("/");

        MvcResult mvcResult = mockMvc.perform(post("/movie")
                .cookie(cookie)
                .cookie(cookie2)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"퀸스 갬빗\", \"page\": 1}"))
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        System.out.println(responseBody);
    }
}
