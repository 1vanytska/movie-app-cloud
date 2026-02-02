package com.project.movieapi.springbootrestapi.dto.movie;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MovieResponseDTO {
    private Long id;
    private String title;
    private Integer year;
    private String genre;

    private DirectorDTO director;

    @Setter
    @Getter
    public static class DirectorDTO {
        private Long id;
        private String name;

    }

}
