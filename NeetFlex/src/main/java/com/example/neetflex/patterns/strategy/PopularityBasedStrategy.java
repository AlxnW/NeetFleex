package com.example.neetflex.patterns.strategy;

import com.example.neetflex.dto.ContentResponseDTO;
import com.example.neetflex.model.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Concrete Strategy: Recommends based on popularity.
 * In this implementation, we simulate popularity by randomly selecting content.
 */
public class PopularityBasedStrategy implements RecommendationStrategy {
    private final Random random = new Random();

    @Override
    public List<ContentResponseDTO> recommend(User user, List<ContentResponseDTO> availableContent) {
        System.out.println("[Strategy: PopularityBased] Generating recommendations for " + user.getUsername() + " based on popularity.");

        if (availableContent == null || availableContent.isEmpty()) {
            System.out.println("[Strategy: PopularityBased] No content available for recommendations.");
            return Collections.emptyList();
        }

        // Create a copy of the available content list to avoid modifying the original
        List<ContentResponseDTO> contentPool = new ArrayList<>(availableContent);
        // Shuffle the list to simulate random selection of "popular" content
        Collections.shuffle(contentPool, random);

        // Select up to 5 items (or fewer if not enough content available)
        int recommendationCount = Math.min(5, contentPool.size());
        List<ContentResponseDTO> recommendations = contentPool.subList(0, recommendationCount);

        System.out.println("[Strategy: PopularityBased] Found " + recommendations.size() + " popular recommendations.");
        return new ArrayList<>(recommendations); // Return a new list to avoid potential issues with the subList view
    }
}
