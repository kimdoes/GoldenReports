package com.example.GoldenReport.DTO.ReviewDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDTO {
    private long video_id;
    private String media_type;
    private String content;
}
