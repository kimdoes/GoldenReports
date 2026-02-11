package com.example.GoldenReport.Controller;

import com.example.GoldenReport.DTO.MovieSerchResultDTO.MovieSearchRequestDTO;
import com.example.GoldenReport.DTO.MovieSerchResultDTO.MovieSearchResultDTO;
import com.example.GoldenReport.Service.Movie.MovieSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movie")
public class MovieSearchController {
    MovieSearchService movieSearchService;

    @Autowired
    public MovieSearchController(MovieSearchService movieSearchService) {
        this.movieSearchService = movieSearchService;
    }

    @PostMapping
    public ResponseEntity<MovieSearchResultDTO> MovieSearch(
            @RequestBody MovieSearchRequestDTO movieSearchRequestDTO) {
        return movieSearchService.SearchMovie(movieSearchRequestDTO);
    }
}
