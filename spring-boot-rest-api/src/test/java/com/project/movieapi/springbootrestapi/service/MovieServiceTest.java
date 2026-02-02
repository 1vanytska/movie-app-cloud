package com.project.movieapi.springbootrestapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.movieapi.springbootrestapi.dto.email.EmailMessageDto;
import com.project.movieapi.springbootrestapi.dto.movie.MovieListRequestDTO;
import com.project.movieapi.springbootrestapi.dto.movie.MovieRequestDTO;
import com.project.movieapi.springbootrestapi.entity.Director;
import com.project.movieapi.springbootrestapi.entity.Movie;
import com.project.movieapi.springbootrestapi.exception.DirectorNotFoundException;
import com.project.movieapi.springbootrestapi.exception.MovieNotFoundException;
import com.project.movieapi.springbootrestapi.exception.UploadFailedException;
import com.project.movieapi.springbootrestapi.repository.DirectorRepository;
import com.project.movieapi.springbootrestapi.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieServiceTest {

    private MovieRepository movieRepository;
    private DirectorRepository directorRepository;
    private RabbitTemplate rabbitTemplate;
    private MovieService movieService;

    @BeforeEach
    void setup() {
        movieRepository = mock(MovieRepository.class);
        directorRepository = mock(DirectorRepository.class);
        rabbitTemplate = mock(RabbitTemplate.class);

        ObjectMapper objectMapper = new ObjectMapper();

        movieService = new MovieService(movieRepository, directorRepository, objectMapper, rabbitTemplate);
    }

    @Test
    void testCreateMovieSuccess() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("Inception");
        dto.setYear(2010);
        dto.setGenre("Sci-Fi");
        dto.setDirectorId(1L);

        Director director = new Director();
        director.setId(1L);
        director.setName("Christopher Nolan");

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        Movie saved = new Movie();
        saved.setId(100L);
        saved.setTitle("Inception");
        saved.setYear(2010);
        saved.setGenre("Sci-Fi");
        saved.setDirector(director);

        when(movieRepository.save(any(Movie.class))).thenReturn(saved);

        var response = movieService.create(dto);

        assertEquals("Inception", response.getTitle());
        assertEquals(2010, response.getYear());
        assertEquals("Sci-Fi", response.getGenre());
        assertEquals("Christopher Nolan", response.getDirector().getName());

        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("email_queue"), any(EmailMessageDto.class));
    }

    @Test
    void testCreateMovieWhenRabbitMqFails() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("Inception");
        dto.setDirectorId(1L);

        Director director = new Director();
        director.setId(1L);
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        Movie saved = new Movie();
        saved.setId(100L);
        saved.setTitle("Inception");
        saved.setDirector(director);
        when(movieRepository.save(any(Movie.class))).thenReturn(saved);

        doThrow(new RuntimeException("RabbitMQ is down"))
                .when(rabbitTemplate).convertAndSend(anyString(), any(Object.class));

        var response = movieService.create(dto);

        assertNotNull(response);
        assertEquals("Inception", response.getTitle());

        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void testCreateMovieDirectorNotFound() {
        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setDirectorId(99L);

        when(directorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DirectorNotFoundException.class, () -> movieService.create(dto));

        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void testGetByIdSuccess() {
        Director director = new Director();
        director.setId(1L);
        director.setName("Nolan");

        Movie movie = new Movie();
        movie.setId(10L);
        movie.setTitle("Interstellar");
        movie.setYear(2014);
        movie.setGenre("Sci-Fi");
        movie.setDirector(director);

        when(movieRepository.findById(10L)).thenReturn(Optional.of(movie));

        var response = movieService.getById(10L);

        assertEquals("Interstellar", response.getTitle());
        assertEquals(2014, response.getYear());
        assertEquals("Sci-Fi", response.getGenre());
        assertEquals("Nolan", response.getDirector().getName());
    }

    @Test
    void testGetByIdNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(MovieNotFoundException.class, () -> movieService.getById(99L));
    }

    @Test
    void testUpdateMovieSuccess() {
        Director director = new Director();
        director.setId(1L);
        director.setName("Nolan");

        Movie movie = new Movie();
        movie.setId(10L);
        movie.setTitle("Old Title");
        movie.setYear(2000);
        movie.setGenre("Drama");
        movie.setDirector(director);

        when(movieRepository.findById(10L)).thenReturn(Optional.of(movie));
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        Movie updated = new Movie();
        updated.setId(10L);
        updated.setTitle("New Title");
        updated.setYear(2020);
        updated.setGenre("Sci-Fi");
        updated.setDirector(director);

        when(movieRepository.save(any(Movie.class))).thenReturn(updated);

        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setTitle("New Title");
        dto.setYear(2020);
        dto.setGenre("Sci-Fi");
        dto.setDirectorId(1L);

        var response = movieService.update(10L, dto);

        assertEquals("New Title", response.getTitle());
        assertEquals(2020, response.getYear());
    }

    @Test
    void testUpdateMovieNotFound() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setDirectorId(1L);

        assertThrows(MovieNotFoundException.class, () -> movieService.update(99L, dto));
    }

    @Test
    void testUpdateDirectorNotFound() {
        Movie movie = new Movie();
        movie.setId(10L);

        when(movieRepository.findById(10L)).thenReturn(Optional.of(movie));
        when(directorRepository.findById(1L)).thenReturn(Optional.empty());

        MovieRequestDTO dto = new MovieRequestDTO();
        dto.setDirectorId(1L);

        assertThrows(DirectorNotFoundException.class, () -> movieService.update(10L, dto));
    }

    @Test
    void testDeleteMovieSuccess() {
        when(movieRepository.existsById(10L)).thenReturn(true);
        movieService.delete(10L);
        verify(movieRepository, times(1)).deleteById(10L);
    }

    @Test
    void testDeleteMovieNotFound() {
        when(movieRepository.existsById(99L)).thenReturn(false);
        assertThrows(MovieNotFoundException.class, () -> movieService.delete(99L));
    }

    @Test
    void testGetAllMovies() {
        Director director = new Director();
        director.setId(1L);
        director.setName("Nolan");

        Movie movie = new Movie();
        movie.setId(10L);
        movie.setTitle("Inception");
        movie.setYear(2010);
        movie.setGenre("Sci-Fi");
        movie.setDirector(director);

        when(movieRepository.findAll()).thenReturn(List.of(movie));

        var list = movieService.getAll();
        assertEquals(1, list.size());
        assertEquals("Inception", list.getFirst().getTitle());
    }

    @Test
    void testListMovies() {
        Director director = new Director();
        director.setId(1L);
        director.setName("Nolan");

        Movie movie = new Movie();
        movie.setId(10L);
        movie.setTitle("Inception");
        movie.setYear(2010);
        movie.setGenre("Sci-Fi");
        movie.setDirector(director);

        Page<Movie> page = new PageImpl<>(List.of(movie));
        when(movieRepository.findAllWithFilters(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        MovieListRequestDTO filter = new MovieListRequestDTO();
        filter.setPage(0);
        filter.setSize(10);

        var response = movieService.list(filter);
        assertEquals(1, response.getList().size());
        assertEquals("Inception", response.getList().getFirst().getTitle());
    }

    @Test
    void testGenerateReport() {
        Director director = new Director();
        director.setId(1L);
        director.setName("Nolan");

        Movie movie = new Movie();
        movie.setId(10L);
        movie.setTitle("Inception");
        movie.setYear(2010);
        movie.setGenre("Sci-Fi");
        movie.setDirector(director);

        Page<Movie> page = new PageImpl<>(List.of(movie));
        when(movieRepository.findAllWithFilters(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        MovieListRequestDTO filter = new MovieListRequestDTO();
        filter.setPage(0);
        filter.setSize(10);

        byte[] report = movieService.generateReport(filter);
        String csv = new String(report);

        assertTrue(csv.contains("Inception"));
        assertTrue(csv.contains("Nolan"));
    }

    @Test
    void testUploadValidJson() {
        String json = """
                    [
                      {"title":"Inception","year":2010,"genre":"Sci-Fi","directorId":1}
                    ]
                """;

        Director director = new Director();
        director.setId(1L);
        director.setName("Nolan");

        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));
        when(movieRepository.save(any(Movie.class))).thenAnswer(inv -> {
            Movie m = inv.getArgument(0);
            m.setId(100L);
            return m;
        });

        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());

        Map<String, Object> result = movieService.upload(inputStream);

        assertEquals(1, result.get("success"));
        assertEquals(0, result.get("failed"));

        verify(rabbitTemplate, times(1)).convertAndSend(eq("email_queue"), any(EmailMessageDto.class));
    }

    @Test
    void testUploadInvalidJson() {
        String invalidJson = "{ \"invalid\": \"json\" }";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidJson.getBytes());

        assertThrows(UploadFailedException.class, () -> movieService.upload(inputStream));

        verifyNoInteractions(rabbitTemplate);
    }
}