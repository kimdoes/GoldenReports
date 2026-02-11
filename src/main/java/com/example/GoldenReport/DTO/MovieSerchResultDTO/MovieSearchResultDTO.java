package com.example.GoldenReport.DTO.MovieSerchResultDTO;

import com.example.GoldenReport.DTO.MovieInfo.MovieResult;
import com.example.GoldenReport.DTO.MovieInfo.TMDBMovieInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieSearchResultDTO {
    private Integer code;
    private Integer amount;
    private List<MovieResult> result;


}
