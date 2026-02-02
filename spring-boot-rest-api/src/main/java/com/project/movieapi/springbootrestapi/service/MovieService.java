package com.project.movieapi.springbootrestapi.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.project.movieapi.springbootrestapi.dto.email.EmailMessageDto;
import com.project.movieapi.springbootrestapi.dto.movie.MovieListRequestDTO;
import com.project.movieapi.springbootrestapi.dto.movie.MovieListResponseDTO;
import com.project.movieapi.springbootrestapi.dto.movie.MovieRequestDTO;
import com.project.movieapi.springbootrestapi.dto.movie.MovieResponseDTO;
import com.project.movieapi.springbootrestapi.entity.Director;
import com.project.movieapi.springbootrestapi.entity.Movie;
import com.project.movieapi.springbootrestapi.exception.DirectorNotFoundException;
import com.project.movieapi.springbootrestapi.exception.MovieNotFoundException;
import com.project.movieapi.springbootrestapi.exception.UploadFailedException;
import com.project.movieapi.springbootrestapi.repository.DirectorRepository;
import com.project.movieapi.springbootrestapi.repository.MovieRepository;
// import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final DirectorRepository directorRepository;
    private final ObjectMapper objectMapper;
    // private final RabbitTemplate rabbitTemplate;

    public MovieService(MovieRepository movieRepository,
                        DirectorRepository directorRepository,
                        ObjectMapper objectMapper
                        // RabbitTemplate rabbitTemplate
                        ) {
        this.movieRepository = movieRepository;
        this.directorRepository = directorRepository;
        this.objectMapper = objectMapper;
        // this.rabbitTemplate = rabbitTemplate;
    }

    public MovieResponseDTO create(MovieRequestDTO dto) {
        Director director = directorRepository.findById(dto.getDirectorId())
                .orElseThrow(() -> new DirectorNotFoundException("Director not found"));

        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setYear(dto.getYear());
        movie.setGenre(dto.getGenre());
        movie.setDirector(director);

        Movie saved = movieRepository.save(movie);

        // try {
        //     EmailMessageDto emailMessage = new EmailMessageDto();
        //     emailMessage.setRecipient("admin@movieapi.com");
        //     emailMessage.setSubject("New Movie Created");
        //     emailMessage.setBody("A new movie has been added to the catalog: " + saved.getTitle()
        //             + " (" + saved.getYear() + ") by " + director.getName());

        //     rabbitTemplate.convertAndSend("email_queue", emailMessage);

        //     System.out.println("Notification sent to RabbitMQ for movie: " + saved.getTitle());
        // } catch (Exception e) {
        //     System.err.println("Failed to send email notification: " + e.getMessage());
        // }

        return toResponseDTO(saved);
    }

    public MovieResponseDTO getById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found"));
        return toResponseDTO(movie);
    }

    public MovieResponseDTO update(Long id, MovieRequestDTO dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie not found"));

        Director director = directorRepository.findById(dto.getDirectorId())
                .orElseThrow(() -> new DirectorNotFoundException("Director not found"));

        movie.setTitle(dto.getTitle());
        movie.setYear(dto.getYear());
        movie.setGenre(dto.getGenre());
        movie.setDirector(director);

        Movie updated = movieRepository.save(movie);
        return toResponseDTO(updated);
    }

    public void delete(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException("Movie not found");
        }
        movieRepository.deleteById(id);
    }

    public List<MovieResponseDTO> getAll() {
        return movieRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public MovieListResponseDTO list(MovieListRequestDTO filter) {
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<Movie> page = movieRepository.findAllWithFilters(
                filter.getDirectorId(),
                filter.getGenre(),
                filter.getYear(),
                pageable
        );

        List<MovieListResponseDTO.MovieShortDTO> list = page.getContent().stream().map(m -> {
            MovieListResponseDTO.MovieShortDTO dto = new MovieListResponseDTO.MovieShortDTO();
            dto.setId(m.getId());
            dto.setTitle(m.getTitle());
            dto.setYear(m.getYear());
            return dto;
        }).toList();

        MovieListResponseDTO response = new MovieListResponseDTO();
        response.setList(list);
        response.setTotalPages(page.getTotalPages());
        return response;
    }

    public byte[] generateReport(MovieListRequestDTO filter) {
        List<Movie> movies = movieRepository.findAllWithFilters(
                filter.getDirectorId(),
                filter.getGenre(),
                filter.getYear(),
                Pageable.unpaged()
        ).getContent();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("Id,Title,Year,Genre,Director");
        for (Movie m : movies) {
            pw.printf("%d,%s,%d,%s,%s%n",
                    m.getId(), m.getTitle(), m.getYear(), m.getGenre(), m.getDirector().getName());
        }
        return sw.toString().getBytes(StandardCharsets.UTF_8);
    }

    public Map<String, Object> upload(InputStream inputStream) {
        int success = 0;
        int failed = 0;

        try (JsonParser parser = objectMapper.getFactory().createParser(inputStream)) {
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new UploadFailedException("Expected JSON array");
            }
            while (parser.nextToken() == JsonToken.START_OBJECT) {
                MovieRequestDTO dto = parser.readValueAs(MovieRequestDTO.class);
                try {
                    create(dto);
                    success++;
                } catch (Exception e) {
                    failed++;
                }
            }
        } catch (IOException e) {
            throw new UploadFailedException("Upload failed: invalid JSON");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("failed", failed);
        return result;
    }

    private MovieResponseDTO toResponseDTO(Movie movie) {
        MovieResponseDTO dto = new MovieResponseDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setYear(movie.getYear());
        dto.setGenre(movie.getGenre());
        MovieResponseDTO.DirectorDTO directorDTO = new MovieResponseDTO.DirectorDTO();
        directorDTO.setId(movie.getDirector().getId());
        directorDTO.setName(movie.getDirector().getName());
        dto.setDirector(directorDTO);
        return dto;
    }
}