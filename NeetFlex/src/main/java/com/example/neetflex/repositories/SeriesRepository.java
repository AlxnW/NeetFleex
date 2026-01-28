package com.example.neetflex.repositories;

import com.example.neetflex.model.contents.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    @Query(value = "SELECT * FROM series ORDER BY RANDOM() LIMIT 10", nativeQuery = true)
    List<Series> findPopularSeries();

    Optional<Series> findById(Long id);
    Optional<Series> findByTitle(String title);
}
