package com.project.movieapi.springbootrestapi.dto.director;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DirectorResponseDTO {
    private Long id;
    private String name;
    private String country;

}