package com.project.movieapi.springbootrestapi.repository;

import com.project.movieapi.springbootrestapi.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m " +
            "WHERE (:directorId IS NULL OR m.director.id = :directorId) " +
            "AND (:genre IS NULL OR m.genre = :genre) " +
            "AND (:year IS NULL OR m.year = :year)")
    Page<Movie> findAllWithFilters(@Param("directorId") Long directorId,
                                   @Param("genre") String genre,
                                   @Param("year") Integer year,
                                   Pageable pageable);
}