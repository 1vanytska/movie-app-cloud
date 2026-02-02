package com.project.movieapi.springbootrestapi.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
class DirectorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateDirector() throws Exception {
        String directorJson = """
            {
              "name": "Christopher Nolan Test",
              "country": "UK"
            }
        """;

        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(directorJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Christopher Nolan Test"))
                .andExpect(jsonPath("$.country").value("UK"));
    }

    @Test
    void testCreateDirectorDuplicate() throws Exception {
        String directorJson = """
            {
              "name": "Unique Director",
              "country": "USA"
            }
        """;

        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(directorJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(directorJson))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetAllDirectors() throws Exception {
        mockMvc.perform(get("/api/directors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testUpdateDirector() throws Exception {
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

        int directorId = JsonPath.read(response, "$.id");

        String updatedJson = """
            {
              "name": "Nolan Updated",
              "country": "USA"
            }
        """;

        mockMvc.perform(put("/api/directors/{id}", directorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nolan Updated"))
                .andExpect(jsonPath("$.country").value("USA"));
    }

    @Test
    void testUpdateDirectorDuplicate() throws Exception {
        String director1Json = """
            {
              "name": "Director One",
              "country": "USA"
            }
        """;
        String response1 = mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(director1Json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        int id1 = JsonPath.read(response1, "$.id");

        String director2Json = """
            {
              "name": "Director Two",
              "country": "UK"
            }
        """;
        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(director2Json))
                .andExpect(status().isCreated());

        String updateJson = """
            {
              "name": "Director Two",
              "country": "USA"
            }
        """;

        mockMvc.perform(put("/api/directors/{id}", id1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isConflict());
    }

    @Test
    void testDeleteDirector() throws Exception {
        String directorJson = """
            {
              "name": "Quentin Tarantino Test",
              "country": "USA"
            }
        """;

        String response = mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(directorJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int directorId = JsonPath.read(response, "$.id");

        mockMvc.perform(delete("/api/directors/{id}", directorId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreateDirectorInvalidName() throws Exception {
        String directorJson = """
            {
              "name": "",
              "country": "UK"
            }
        """;

        mockMvc.perform(post("/api/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(directorJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateDirectorNotFound() throws Exception {
        String updatedJson = """
            {
              "name": "Nonexistent",
              "country": "Unknown"
            }
        """;

        mockMvc.perform(put("/api/directors/{id}", 9999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteDirectorNotFound() throws Exception {
        mockMvc.perform(delete("/api/directors/{id}", 9999))
                .andExpect(status().isNotFound());
    }
}