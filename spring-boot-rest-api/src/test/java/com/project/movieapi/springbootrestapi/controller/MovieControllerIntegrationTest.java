package com.project.movieapi.springbootrestapi.controller;

import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private int directorId;

    @BeforeEach
    void setup() throws Exception {
        String directorJson = """
            {
              "name": "Christopher Nolan Test",
              "country": "UK"
            }
        """;

        String response = mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(directorJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        directorId = JsonPath.read(response, "$.id");
    }

    @Test
    void testCreateMovie() throws Exception {
        String movieJson = """
            {
              "title": "Inception Test",
              "year": 2010,
              "genre": "Sci-Fi",
              "directorId": %d
            }
        """.formatted(directorId);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Inception Test"));
    }

    @Test
    void testGetMovieById() throws Exception {
        String movieJson = """
            {
              "title": "Inception Test",
              "year": 2010,
              "genre": "Sci-Fi",
              "directorId": %d
            }
        """.formatted(directorId);

        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int movieId = JsonPath.read(response, "$.id");

        mockMvc.perform(get("/api/movies/{id}", movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movieId))
                .andExpect(jsonPath("$.director").exists());
    }

    @Test
    void testUploadMovies() throws Exception {
        String jsonArray = """
            [
              {"title":"Tenet Test","year":2020,"genre":"Sci-Fi","directorId":%d},
              {"title":"Dunkirk Test","year":2017,"genre":"War","directorId":%d}
            ]
        """.formatted(directorId, directorId);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "movies.json",
                MediaType.APPLICATION_JSON_VALUE,
                jsonArray.getBytes()
        );

        mockMvc.perform(multipart("/api/movies/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(2))
                .andExpect(jsonPath("$.failed").value(0));
    }

    @Test
    void testUploadInvalidJson() throws Exception {
        String invalidJson = """
            { "invalid": "json" }
        """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid.json",
                MediaType.APPLICATION_JSON_VALUE,
                invalidJson.getBytes()
        );

        mockMvc.perform(multipart("/api/movies/upload")
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadEmptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.json",
                MediaType.APPLICATION_JSON_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/movies/upload")
                        .file(emptyFile))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("File is empty")));
    }

    @Test
    void testDeleteMovie() throws Exception {
        String movieJson = """
            {
              "title": "Inception Test",
              "year": 2010,
              "genre": "Sci-Fi",
              "directorId": %d
            }
        """.formatted(directorId);

        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int movieId = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/movies/{id}", movieId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllMovies() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testListMoviesWithPagination() throws Exception {
        String filterJson = """
            {
              "directorId": %d,
              "page": 0,
              "size": 10
            }
        """.formatted(directorId);

        mockMvc.perform(post("/api/movies/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void testGenerateReport() throws Exception {
        String filterJson = """
            {
              "directorId": %d
            }
        """.formatted(directorId);

        mockMvc.perform(post("/api/movies/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filterJson))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        Matchers.containsString("movies_report.csv")));
    }

    @Test
    void testCreateMovieInvalidTitle() throws Exception {
        String movieJson = """
            {
              "title": "",
              "year": 2010,
              "genre": "Sci-Fi",
              "directorId": %d
            }
        """.formatted(directorId);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateMovieInvalidYear() throws Exception {
        String movieJson = """
            {
              "title": "Invalid Year Movie",
              "year": -5,
              "genre": "Sci-Fi",
              "directorId": %d
            }
        """.formatted(directorId);

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetMovieNotFound() throws Exception {
        mockMvc.perform(get("/api/movies/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateMovieNotFound() throws Exception {
        String updatedJson = """
            {
              "title": "Nonexistent",
              "year": 2020,
              "genre": "Sci-Fi",
              "directorId": %d
            }
        """.formatted(directorId);

        mockMvc.perform(put("/api/movies/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteMovieNotFound() throws Exception {
        mockMvc.perform(delete("/api/movies/{id}", 9999))
                .andExpect(status().isNotFound());
    }
}