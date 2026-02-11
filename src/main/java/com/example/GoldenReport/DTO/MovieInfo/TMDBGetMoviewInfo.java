package com.example.GoldenReport.DTO.MovieInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TMDBGetMoviewInfo {
    int page;
    TMDBMovieInfo[] results;

    int total_pages;
    int total_results;
}
