package com.example.GoldenReport.Service.Movie;

import com.example.GoldenReport.DTO.MovieInfo.MovieResult;
import com.example.GoldenReport.DTO.MovieInfo.TMDBGetMoviewInfo;
import com.example.GoldenReport.DTO.MovieInfo.TMDBMovieInfo;
import com.example.GoldenReport.DTO.MovieSerchResultDTO.MovieSearchRequestDTO;
import com.example.GoldenReport.DTO.MovieSerchResultDTO.MovieSearchResultDTO;
import com.example.GoldenReport.Repository.MemberRepository;
import com.example.GoldenReport.Service.JWT.JWTFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class MovieSearchService {
    private final RestTemplate restTemplate;
    private final JWTFilter jwtFilter;
    private final MemberRepository memberRepository;

    @Autowired
    public MovieSearchService(RestTemplate restTemplate,
                              JWTFilter jwtFilter,
                              MemberRepository memberRepository) {
        this.restTemplate = restTemplate;
        this.jwtFilter = jwtFilter;
        this.memberRepository = memberRepository;
    }

    @Value("${tmdb.url.normal}")
    private String tmdbUrl;

    @Value("${tmdb.url.added}")
    private String tmdbUrlPlus;
    
    public ResponseEntity<?> SearchMovie(HttpServletRequest httpServletRequest,
            MovieSearchRequestDTO movieSearchRequestDTO){
        try {
            Optional<String> optionalUserId = jwtFilter.getPlainString(httpServletRequest);

            if (optionalUserId.isEmpty()) {
                throw new NullPointerException("JWT Token is Empty");
            }

            String userId = optionalUserId.get();
            boolean isMember = memberRepository.existsById(userId);

            if (isMember) {
                return SearchMovie(movieSearchRequestDTO);
            } else {
                return ResponseEntity
                        .status(HttpStatus.FOUND)
                        .location(URI.create("/signup"))
                        .build();
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            return ErrorHandler();
        }
    }

    private ResponseEntity<MovieSearchResultDTO> SearchMovie(MovieSearchRequestDTO movieSearchRequestDTO) {
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

    private ResponseEntity<?> ErrorHandler() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/naver"))
                .build();
    }
}
