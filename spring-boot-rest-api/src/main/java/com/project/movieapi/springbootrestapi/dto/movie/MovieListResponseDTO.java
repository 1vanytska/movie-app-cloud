package com.project.movieapi.springbootrestapi.dto.movie;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MovieListResponseDTO {
    private List<MovieShortDTO> list;
    private int totalPages;

    @Setter
    @Getter
    public static class MovieShortDTO {
        private Long id;
        private String title;
        private Integer year;

    }
}
