package com.project.movieapi.springbootrestapi.dto.movie;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MovieRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidMovieRequestDTO() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("Inception");
        dto.setYear(2010);
        dto.setGenre("Sci-Fi");
        dto.setDirectorId(1L);

        Set<ConstraintViolation<MovieRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO should be valid when all fields are correct");
    }

    @Test
    void testTitleIsBlank() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("");
        dto.setYear(2010);
        dto.setGenre("Sci-Fi");
        dto.setDirectorId(1L);

        Set<ConstraintViolation<MovieRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should be invalid when title is blank");
    }

    @Test
    void testTitleIsNull() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle(null);
        dto.setYear(2010);
        dto.setGenre("Sci-Fi");
        dto.setDirectorId(1L);

        Set<ConstraintViolation<MovieRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should be invalid when title is null");
    }

    @Test
    void testYearIsNull() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("Inception");
        dto.setYear(null);
        dto.setGenre("Sci-Fi");
        dto.setDirectorId(1L);

        Set<ConstraintViolation<MovieRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should be invalid when year is null");
    }

    @Test
    void testYearTooSmall() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("Old Movie");
        dto.setYear(1800);
        dto.setGenre("Drama");
        dto.setDirectorId(1L);

        Set<ConstraintViolation<MovieRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should be invalid when year < 1890");
    }

    @Test
    void testYearTooLarge() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("Future Movie");
        dto.setYear(2200);
        dto.setGenre("Sci-Fi");
        dto.setDirectorId(1L);

        Set<ConstraintViolation<MovieRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should be invalid when year > 2115");
    }

    @Test
    void testGenreIsBlank() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("Inception");
        dto.setYear(2010);
        dto.setGenre("");
        dto.setDirectorId(1L);

        Set<ConstraintViolation<MovieRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should be invalid when genre is blank");
    }

    @Test
    void testDirectorIdIsNull() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("Inception");
        dto.setYear(2010);
        dto.setGenre("Sci-Fi");
        dto.setDirectorId(null);

        Set<ConstraintViolation<MovieRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should be invalid when directorId is null");
    }
}