package com.example.neetflex.patterns.strategy;

import com.example.neetflex.dto.ContentResponseDTO;
import com.example.neetflex.model.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Concrete Strategy: Recommends based on genre.
 * In this implementation, we filter content by a randomly selected genre.
 */
public class GenreBasedStrategy implements RecommendationStrategy {
    private final Random random = new Random();

    // Sample genres to use if user has no preferences
    private final String[] commonGenres = {
        "Action", "Comedy", "Drama", "Sci-Fi", "Horror", 
        "Romance", "Thriller", "Documentary", "Animation"
    };

    @Override
    public List<ContentResponseDTO> recommend(User user, List<ContentResponseDTO> availableContent) {
        System.out.println("[Strategy: GenreBased] Generating recommendations for " + user.getUsername() + " based on genres.");

        if (availableContent == null || availableContent.isEmpty()) {
            System.out.println("[Strategy: GenreBased] No content available for recommendations.");
            return Collections.emptyList();
        }

        // Select a random genre to recommend
        // In a real implementation, this would come from user preferences
        String selectedGenre = commonGenres[random.nextInt(commonGenres.length)];
        System.out.println("[Strategy: GenreBased] Selected genre: " + selectedGenre);

        // Filter content by the selected genre
        List<ContentResponseDTO> genreMatches = availableContent.stream()
            .filter(content -> content.getGenre() != null && 
                   content.getGenre().toLowerCase().contains(selectedGenre.toLowerCase()))
            .collect(Collectors.toList());

        // If no matches for the selected genre, return a subset of random content
        if (genreMatches.isEmpty()) {
            System.out.println("[Strategy: GenreBased] No content found for genre: " + selectedGenre + ". Using random selection.");
            List<ContentResponseDTO> randomContent = new ArrayList<>(availableContent);
            Collections.shuffle(randomContent, random);
            int count = Math.min(3, randomContent.size());
            genreMatches = randomContent.subList(0, count);
        }

        System.out.println("[Strategy: GenreBased] Found " + genreMatches.size() + " genre-based recommendations.");
        return new ArrayList<>(genreMatches);
    }
}
