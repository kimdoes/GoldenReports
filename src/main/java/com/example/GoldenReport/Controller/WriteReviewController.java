package com.example.GoldenReport.Controller;

import com.example.GoldenReport.DTO.HTTPResponseDTO.HTTPResponseDTO;
import com.example.GoldenReport.DTO.ReviewDTO.ReviewRequestDTO;
import com.example.GoldenReport.Service.Movie.WriteReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("review")
public class WriteReviewController {
    WriteReviewService writeReviewService;

    public WriteReviewController(WriteReviewService writeReviewService) {
        this.writeReviewService = writeReviewService;
    }

    @PostMapping()
    public ResponseEntity<HTTPResponseDTO> writeReview(@RequestBody ReviewRequestDTO reviewRequestDTO) {
        return writeReviewService.saveReview(reviewRequestDTO);
    }
}
