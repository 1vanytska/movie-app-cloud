package com.project.movieapi.springbootrestapi.service;

import com.project.movieapi.springbootrestapi.dto.director.DirectorRequestDTO;
import com.project.movieapi.springbootrestapi.dto.director.DirectorResponseDTO;
import com.project.movieapi.springbootrestapi.entity.Director;
import com.project.movieapi.springbootrestapi.exception.DirectorNotFoundException;
import com.project.movieapi.springbootrestapi.exception.ResourceAlreadyExistsException;
import com.project.movieapi.springbootrestapi.repository.DirectorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DirectorService {
    private final DirectorRepository directorRepository;

    public DirectorService(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public DirectorResponseDTO create(DirectorRequestDTO dto) {
        if (directorRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ResourceAlreadyExistsException("Director with name '" + dto.getName() + "' already exists");
        }
        Director director = new Director();
        director.setName(dto.getName());
        director.setCountry(dto.getCountry());

        Director saved = directorRepository.save(director);
        return toResponseDTO(saved);
    }

    public List<DirectorResponseDTO> getAll() {
        return directorRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public DirectorResponseDTO update(Long id, DirectorRequestDTO dto) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new DirectorNotFoundException("Director not found"));

        if (directorRepository.existsByNameIgnoreCase(dto.getName())
                && !director.getName().equalsIgnoreCase(dto.getName())) {
            throw new ResourceAlreadyExistsException("Director with name '" + dto.getName() + "' already exists");
        }

        director.setName(dto.getName());
        director.setCountry(dto.getCountry());

        Director updated = directorRepository.save(director);
        return toResponseDTO(updated);
    }

    public void delete(Long id) {
        if (!directorRepository.existsById(id)) {
            throw new DirectorNotFoundException("Director not found");
        }
        directorRepository.deleteById(id);
    }

    private DirectorResponseDTO toResponseDTO(Director director) {
        DirectorResponseDTO dto = new DirectorResponseDTO();
        dto.setId(director.getId());
        dto.setName(director.getName());
        dto.setCountry(director.getCountry());
        return dto;
    }
}