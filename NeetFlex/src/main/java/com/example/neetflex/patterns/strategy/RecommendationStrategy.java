package com.example.neetflex.patterns.strategy;

import com.example.neetflex.dto.ContentResponseDTO;
import com.example.neetflex.model.user.User;
import java.util.List;

/**
 * Strategy Interface: Defines the contract for recommendation algorithms.
 */
public interface RecommendationStrategy {
    /**
     * Generates recommendations based on a specific algorithm.
     * @param user The user needing recommendations.
     * @param availableContent Pool of content to choose from.
     * @return A list of recommended ContentResponseDTO objects.
     */
    List<ContentResponseDTO> recommend(User user, List<ContentResponseDTO> availableContent);
}
