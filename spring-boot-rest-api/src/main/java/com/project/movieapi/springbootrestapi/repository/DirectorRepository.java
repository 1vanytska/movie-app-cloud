package com.project.movieapi.springbootrestapi.repository;

import com.project.movieapi.springbootrestapi.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    boolean existsByNameIgnoreCase(String name);
}