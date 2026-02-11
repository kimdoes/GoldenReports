package com.example.GoldenReport.Service.Movie;

import com.example.GoldenReport.DTO.HTTPResponseDTO.HTTPResponseDTO;
import com.example.GoldenReport.DTO.ReviewDTO.ReviewRequestDTO;
import com.example.GoldenReport.Domain.Review;
import com.example.GoldenReport.Repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class WriteReviewService {
    ReviewRepository reviewRepository;

    public WriteReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ResponseEntity<HTTPResponseDTO> saveReview(ReviewRequestDTO reviewRequestDTO) {
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
}
