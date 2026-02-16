package com.example.GoldenReport.Controller;

import com.example.GoldenReport.DTO.HTTPResponseDTO.HTTPResponseDTO;
import com.example.GoldenReport.DTO.ReviewDTO.ReviewRequestDTO;
import com.example.GoldenReport.Service.Movie.WriteReviewService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
public class WriteReviewController {
    WriteReviewService writeReviewService;

    public WriteReviewController(WriteReviewService writeReviewService) {
        this.writeReviewService = writeReviewService;
    }

    @PostMapping()
    public ResponseEntity<?> writeReview(
            HttpServletRequest request,
            @RequestBody ReviewRequestDTO reviewRequestDTO) {
        return writeReviewService.saveReview(request, reviewRequestDTO);
    }
}
