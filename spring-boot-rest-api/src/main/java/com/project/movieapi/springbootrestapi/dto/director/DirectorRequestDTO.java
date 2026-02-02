package com.project.movieapi.springbootrestapi.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DirectorRequestDTO {
    @NotBlank(message = "Director name is mandatory")
    @Size(max = 255)
    private String name;

    private String country;

}