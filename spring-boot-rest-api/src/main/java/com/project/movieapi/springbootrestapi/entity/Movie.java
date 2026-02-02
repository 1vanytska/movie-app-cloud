package com.project.movieapi.springbootrestapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(
        name = "movies",
        indexes = {
                @Index(name = "idx_movies_director", columnList = "director_id"),
                @Index(name = "idx_movies_genre", columnList = "genre"),
                @Index(name = "idx_movies_year", columnList = "year")
        }
)
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 255)
    private String title;

    @NotNull(message = "Year is mandatory")
    private Integer year;

    @NotBlank
    private String genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;
}