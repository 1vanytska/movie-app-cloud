package com.project.movieapi.springbootrestapi.dto.movie;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MovieRequestDTO {
    @NotBlank
    private String title;

    @NotNull
    @Min(1890)
    @Max(2115)
    private Integer year;

    @NotBlank
    private String genre;

    @NotNull
    private Long directorId;

}