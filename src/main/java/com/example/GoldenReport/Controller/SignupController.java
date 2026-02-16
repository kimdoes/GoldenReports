package com.example.GoldenReport.Controller;

import com.example.GoldenReport.DTO.HTTPResponseDTO.HTTPResponseDTO;
import com.example.GoldenReport.DTO.Signup.SignupRequestDTO;
import com.example.GoldenReport.Service.LogInAndSignUp.SignupService;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/signup")
public class SignupController {
    SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping
    public ResponseEntity<HTTPResponseDTO> signup(
            @ModelAttribute SignupRequestDTO signupRequestDTO,
            @CookieValue(value="Authentication_Signup", required = false) String token) {
        System.out.println("signup...");
        return signupService.signup(token, signupRequestDTO);
    }
}
