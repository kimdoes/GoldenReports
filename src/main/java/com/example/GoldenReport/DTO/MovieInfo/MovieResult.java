package com.example.GoldenReport.DTO.MovieInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResult {
    private Integer index;
    private TMDBMovieInfo movieInfo;
}
