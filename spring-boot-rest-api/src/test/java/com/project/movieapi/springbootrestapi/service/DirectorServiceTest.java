package com.project.movieapi.springbootrestapi.service;

import com.project.movieapi.springbootrestapi.dto.director.DirectorRequestDTO;
import com.project.movieapi.springbootrestapi.entity.Director;
import com.project.movieapi.springbootrestapi.exception.DirectorNotFoundException;
import com.project.movieapi.springbootrestapi.exception.ResourceAlreadyExistsException;
import com.project.movieapi.springbootrestapi.repository.DirectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DirectorServiceTest {

    private DirectorRepository directorRepository;
    private DirectorService directorService;

    @BeforeEach
    void setup() {
        directorRepository = mock(DirectorRepository.class);
        directorService = new DirectorService(directorRepository);
    }

    @Test
    void testCreateDirectorSuccess() {
        DirectorRequestDTO dto = new DirectorRequestDTO();
        dto.setName("Christopher Nolan");
        dto.setCountry("UK");

        when(directorRepository.existsByNameIgnoreCase("Christopher Nolan")).thenReturn(false);

        Director saved = new Director();
        saved.setId(1L);
        saved.setName("Christopher Nolan");
        saved.setCountry("UK");

        when(directorRepository.save(any(Director.class))).thenReturn(saved);

        var response = directorService.create(dto);

        assertEquals("Christopher Nolan", response.getName());
        assertEquals("UK", response.getCountry());
    }

    @Test
    void testUpdateDirectorDuplicateName() {
        Director existing = new Director();
        existing.setId(1L);
        existing.setName("Nolan");
        existing.setCountry("UK");

        when(directorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(directorRepository.existsByNameIgnoreCase("Tarantino")).thenReturn(true);

        DirectorRequestDTO dto = new DirectorRequestDTO();
        dto.setName("Tarantino");
        dto.setCountry("USA");

        assertThrows(ResourceAlreadyExistsException.class, () -> directorService.update(1L, dto));
    }

    @Test
    void testGetAllDirectors() {
        Director d1 = new Director();
        d1.setId(1L);
        d1.setName("Nolan");
        d1.setCountry("UK");

        Director d2 = new Director();
        d2.setId(2L);
        d2.setName("Tarantino");
        d2.setCountry("USA");

        when(directorRepository.findAll()).thenReturn(List.of(d1, d2));

        var list = directorService.getAll();

        assertEquals(2, list.size());
        assertEquals("Nolan", list.get(0).getName());
        assertEquals("Tarantino", list.get(1).getName());
    }

    @Test
    void testUpdateDirectorSuccess() {
        Director existing = new Director();
        existing.setId(1L);
        existing.setName("Nolan");
        existing.setCountry("UK");

        when(directorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(directorRepository.existsByNameIgnoreCase("Nolan Updated")).thenReturn(false);

        Director updated = new Director();
        updated.setId(1L);
        updated.setName("Nolan Updated");
        updated.setCountry("USA");

        when(directorRepository.save(any(Director.class))).thenReturn(updated);

        DirectorRequestDTO dto = new DirectorRequestDTO();
        dto.setName("Nolan Updated");
        dto.setCountry("USA");

        var response = directorService.update(1L, dto);

        assertEquals("Nolan Updated", response.getName());
        assertEquals("USA", response.getCountry());
    }

    @Test
    void testUpdateDirectorNotFound() {
        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        DirectorRequestDTO dto = new DirectorRequestDTO();
        dto.setName("Someone");
        dto.setCountry("Unknown");

        assertThrows(DirectorNotFoundException.class, () -> directorService.update(99L, dto));
    }

    @Test
    void testDeleteDirectorSuccess() {
        when(directorRepository.existsById(1L)).thenReturn(true);

        directorService.delete(1L);

        verify(directorRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDirectorNotFound() {
        when(directorRepository.existsById(99L)).thenReturn(false);

        assertThrows(DirectorNotFoundException.class, () -> directorService.delete(99L));
    }
}