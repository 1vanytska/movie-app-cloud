package com.project.movieapi.springbootrestapi.dto.director;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DirectorRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidDirectorRequestDTO() {
        DirectorRequestDTO dto = new DirectorRequestDTO();
        dto.setName("Christopher Nolan");
        dto.setCountry("UK");

        Set<ConstraintViolation<DirectorRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "DTO should be valid when all fields are correct");
    }

    @Test
    void testNameIsBlank() {
        DirectorRequestDTO dto = new DirectorRequestDTO();
        dto.setName("");
        dto.setCountry("UK");

        Set<ConstraintViolation<DirectorRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should be invalid when name is blank");
    }

    @Test
    void testNameIsNull() {
        DirectorRequestDTO dto = new DirectorRequestDTO();
        dto.setName(null);
        dto.setCountry("USA");

        Set<ConstraintViolation<DirectorRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should be invalid when name is null");
    }

    @Test
    void testNameTooLong() {
        DirectorRequestDTO dto = new DirectorRequestDTO();
        dto.setName("A".repeat(300));
        dto.setCountry("USA");

        Set<ConstraintViolation<DirectorRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "DTO should be invalid when name exceeds 255 characters");
    }
}