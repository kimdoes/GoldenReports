package com.example.GoldenReport.DTO.MovieSerchResultDTO;

import lombok.Data;

@Data
public class MovieSearchRequestDTO {
    private String query;
    private int page;
}
