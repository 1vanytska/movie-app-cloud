package com.project.movieapi.springbootrestapi.controller;

import com.project.movieapi.springbootrestapi.dto.director.DirectorRequestDTO;
import com.project.movieapi.springbootrestapi.dto.director.DirectorResponseDTO;
import com.project.movieapi.springbootrestapi.service.DirectorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directors")
public class DirectorController {
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public ResponseEntity<List<DirectorResponseDTO>> getAll() {
        return ResponseEntity.ok(directorService.getAll());
    }

    @PostMapping
    public ResponseEntity<DirectorResponseDTO> create(@Valid @RequestBody DirectorRequestDTO dto) {
        DirectorResponseDTO created = directorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DirectorResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody DirectorRequestDTO dto) {
        return ResponseEntity.ok(directorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        directorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
