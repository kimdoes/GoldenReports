package com.example.GoldenReport.DTO.MovieInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TMDBMovieInfo {
    int id;
    String media_type;

    String name;            //for tv
    String title;           //for movie

    String overview;
    int[] genre_ids;

    String first_air_date;  //for tv
    String release_date;    //for movie

    boolean adult;
}
