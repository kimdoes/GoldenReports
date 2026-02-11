package com.example.GoldenReport.Service.Movie;

import com.example.GoldenReport.DTO.MovieInfo.MovieResult;
import com.example.GoldenReport.DTO.MovieInfo.TMDBGetMoviewInfo;
import com.example.GoldenReport.DTO.MovieInfo.TMDBMovieInfo;
import com.example.GoldenReport.DTO.MovieSerchResultDTO.MovieSearchRequestDTO;
import com.example.GoldenReport.DTO.MovieSerchResultDTO.MovieSearchResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieSearchService {
    private final RestTemplate restTemplate;

    @Autowired
    public MovieSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${tmdb.url.normal}")
    private String tmdbUrl;

    @Value("${tmdb.url.added}")
    private String tmdbUrlPlus;

    public ResponseEntity<MovieSearchResultDTO> SearchMovie(MovieSearchRequestDTO movieSearchRequestDTO) {
        try {
            int count = 0;
            List<MovieResult> movieResultList = new ArrayList<>();

            String query = movieSearchRequestDTO.getQuery();
            String queryToURL = URLEncoder.encode(query, "UTF-8");
            int page = movieSearchRequestDTO.getPage();

            TMDBGetMoviewInfo getMovieFromTMDB = restTemplate.getForObject(tmdbUrl + queryToURL + tmdbUrlPlus + page,
                    TMDBGetMoviewInfo.class);
            TMDBMovieInfo[] result = getMovieFromTMDB.getResults();


            for (TMDBMovieInfo movieInfo : result) {
                MovieResult movieResult = MovieResult.builder()
                        .index(count+1)
                        .movieInfo(movieInfo)
                        .build();

                movieResultList.add(movieResult);
                count++;
            }

            MovieSearchResultDTO movieSearchResultDTO = MovieSearchResultDTO.builder()
                    .code(200)
                    .amount(movieResultList.size())
                    .result(movieResultList)
                    .build();
            return ResponseEntity.ok(movieSearchResultDTO);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            MovieSearchResultDTO movieSearchResultDTO = MovieSearchResultDTO.builder()
                    .code(400)
                    .amount(0)
                    .build();

            return ResponseEntity.badRequest().body(movieSearchResultDTO);
        }
    }
}
