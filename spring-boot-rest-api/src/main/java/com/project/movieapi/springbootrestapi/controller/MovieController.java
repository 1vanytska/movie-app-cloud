package com.project.movieapi.springbootrestapi.controller;

import com.project.movieapi.springbootrestapi.dto.movie.MovieRequestDTO;
import com.project.movieapi.springbootrestapi.dto.movie.MovieResponseDTO;
import com.project.movieapi.springbootrestapi.dto.movie.MovieListRequestDTO;
import com.project.movieapi.springbootrestapi.dto.movie.MovieListResponseDTO;
import com.project.movieapi.springbootrestapi.exception.EmptyFileException;
import com.project.movieapi.springbootrestapi.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<MovieResponseDTO> create(@Valid @RequestBody MovieRequestDTO movieRequest) {
        MovieResponseDTO created = movieService.create(movieRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(movieService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> update(@PathVariable Long id,
                                                   @Valid @RequestBody MovieRequestDTO movieRequest) {
        return ResponseEntity.ok(movieService.update(id, movieRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> getAll() {
        return ResponseEntity.ok(movieService.getAll());
    }

    @PostMapping("/_list")
    public ResponseEntity<MovieListResponseDTO> list(@RequestBody MovieListRequestDTO filter) {
        return ResponseEntity.ok(movieService.list(filter));
    }

    @PostMapping("/_report")
    public ResponseEntity<byte[]> report(@RequestBody MovieListRequestDTO filter) {
        byte[] csvBytes = movieService.generateReport(filter);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movies_report.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvBytes);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new EmptyFileException("File is empty");
        }
        return ResponseEntity.ok(movieService.upload(file.getInputStream()));
    }
}