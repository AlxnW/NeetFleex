package com.example.neetflex.repositories;

import com.example.neetflex.model.contents.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query("SELECT m FROM Movie m")
    List<Movie> findAllMovies();

    @Query(value = "SELECT * FROM movie ORDER BY RANDOM() LIMIT 10", nativeQuery = true)
    List<Movie> findPopularMovies();

    Optional<Movie> findById(Long id);
    Optional<Movie> findByTitle(String title);

}
