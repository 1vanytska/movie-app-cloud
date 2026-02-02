package com.project.movieapi.springbootrestapi.dto.movie;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MovieListRequestDTO {
    private Long directorId;
    private String genre;
    private Integer year;
    private int page = 0;
    private int size = 20;

}
