package com.example.GoldenReport.Service.Movie;

import com.example.GoldenReport.DTO.HTTPResponseDTO.HTTPResponseDTO;
import com.example.GoldenReport.DTO.ReviewDTO.ReviewRequestDTO;
import com.example.GoldenReport.Domain.Review;
import com.example.GoldenReport.Repository.MemberRepository;
import com.example.GoldenReport.Repository.ReviewRepository;
import com.example.GoldenReport.Service.JWT.JWTFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

@Service
public class WriteReviewService {
    ReviewRepository reviewRepository;
    MemberRepository memberRepository;
    JWTFilter jwtFilter;

    public WriteReviewService(ReviewRepository reviewRepository,
                              JWTFilter jwtFilter,
                              MemberRepository memberRepository) {
        this.reviewRepository = reviewRepository;
        this.jwtFilter = jwtFilter;
        this.memberRepository = memberRepository;
    }

    public ResponseEntity<?> saveReview(HttpServletRequest httpServletRequest,
                                         ReviewRequestDTO reviewRequestDTO) {
        try {
            Optional<String> optionalUserId = jwtFilter.getPlainString(httpServletRequest);

            if (optionalUserId.isEmpty()) {
                throw new NullPointerException();
            }

            String userId = optionalUserId.get();
            boolean isMember = memberRepository.existsById(userId);

            if (isMember) {
                return saveReview(reviewRequestDTO);
            } else {
                return ResponseEntity
                        .status(HttpStatus.FOUND)
                        .location(URI.create("/signup"))
                        .build();
            }

        } catch (NullPointerException e) {
            return ErrorHandler();
        }

    }

    private ResponseEntity<HTTPResponseDTO> saveReview(ReviewRequestDTO reviewRequestDTO) {
        try {
            Review review = Review.builder()
                    .content(reviewRequestDTO.getContent())
                    .media_type(reviewRequestDTO.getMedia_type())
                    .movieId(reviewRequestDTO.getVideo_id())
                    .memberId(1010101010)       //로그인 기능 작성 후 추가
                    .build();
            reviewRepository.save(review);

            HTTPResponseDTO response = HTTPResponseDTO.builder()
                    .status(200)
                    .message("성공!!")
                    .build();
            return ResponseEntity.ok(response);

        } catch (Exception e) {

            HTTPResponseDTO response = HTTPResponseDTO.builder()
                    .status(500)
                    .message("Internal Server Error")
                    .build();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    private ResponseEntity<?> ErrorHandler() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/naver"))
                .build();
    }
}
