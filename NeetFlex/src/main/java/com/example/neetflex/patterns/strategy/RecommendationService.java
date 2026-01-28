package com.example.neetflex.patterns.strategy;

import com.example.neetflex.dto.ContentResponseDTO;
import com.example.neetflex.model.user.User;
import com.example.neetflex.service.impl.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {
    // The service holds a reference to one strategy object
    private RecommendationStrategy currentStrategy = new PopularityBasedStrategy(); // Set a default

    private final ContentService contentService;

    @Autowired
    public RecommendationService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setStrategy(RecommendationStrategy strategy) {
        System.out.println("[Service: Recommendation] Changing strategy to: " + strategy.getClass().getSimpleName());
        this.currentStrategy = strategy;
    }

    /**
     * Generates recommendations for a user using the current strategy.
     * @param user The user to generate recommendations for
     * @return A list of recommended content
     */
    public List<ContentResponseDTO> generateRecommendations(User user) {
        System.out.println("[Service: Recommendation] Generating recommendations for " + user.getUsername() + " using strategy: " + currentStrategy.getClass().getSimpleName());

        // Fetch available content from repositories
        List<ContentResponseDTO> allContent = fetchAvailableContent();

        // Delegate the actual algorithm to the strategy object
        List<ContentResponseDTO> results = currentStrategy.recommend(user, allContent);

        System.out.println("[Service: Recommendation] Strategy returned " + results.size() + " items.");
        return results;
    }

    /**
     * Fetches available content from the ContentService.
     * @return A list of all available content
     */
    private List<ContentResponseDTO> fetchAvailableContent() {
        System.out.println("[Service: Recommendation] Fetching available content...");

        List<ContentResponseDTO> allContent = new ArrayList<>();

        // Get movies and series from the ContentService
        try {
            List<ContentResponseDTO> movies = contentService.getAllPopularMovies();
            List<ContentResponseDTO> series = contentService.getAllPopularSeries();

            if (movies != null) {
                allContent.addAll(movies);
            }

            if (series != null) {
                allContent.addAll(series);
            }

            System.out.println("[Service: Recommendation] Fetched " + allContent.size() + " content items.");
        } catch (Exception e) {
            System.err.println("[Service: Recommendation] Error fetching content: " + e.getMessage());
        }

        return allContent;
    }
}
