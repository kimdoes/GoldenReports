package com.example.GoldenReport.Service.LogInAndSignUp;

import com.example.GoldenReport.DTO.HTTPResponseDTO.HTTPResponseDTO;
import com.example.GoldenReport.DTO.Signup.SignupRequestDTO;
import com.example.GoldenReport.Domain.Member;
import com.example.GoldenReport.Domain.position;
import com.example.GoldenReport.Repository.MemberRepository;
import com.example.GoldenReport.Service.JWT.JWTFilter;
import com.example.GoldenReport.Service.JWT.JWTProvider;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

@Service
public class SignupService {
    MemberRepository memberRepository;
    JWTProvider jwtProvider;

    public SignupService(MemberRepository memberRepository,
                         JWTProvider jwtProvider) {
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
    }

    public ResponseEntity<HTTPResponseDTO> signup(String token, SignupRequestDTO signupRequestDTO){
        try {
            if (token == null) {
                throw new RuntimeException("No token in cookie");
            }

            String username = signupRequestDTO.getUsername();
            Optional<String> decodeJWT = jwtProvider.decodeJWT(token);

            if(decodeJWT == null) {
                throw new RuntimeException("Unsupported JWT");
            }

            String userId = decodeJWT.get();

            Member member = Member.builder()
                    .id(userId)
                    .username(username)
                    .position(position.USER)
                    .build();

            memberRepository.save(member);

            HTTPResponseDTO response = HTTPResponseDTO.builder()
                    .status(200)
                    .message("Signup Successful")
                    .build();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);

        } catch (RuntimeException e){
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create("/oauth2/authorization/naver"))
                    .build();
        }
    }
}