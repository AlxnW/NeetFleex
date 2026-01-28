package com.example.neetflex.patterns.chainofresponsibility;

import com.example.neetflex.enums.SubscriptionType;
import com.example.neetflex.model.contents.Content;
import com.example.neetflex.model.user.Subscription;
import com.example.neetflex.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete filter that filters content based on the user's subscription type.
 * Premium content is only available to users with Premium or Family subscriptions.
 */
public class SubscriptionBasedFilter extends AbstractContentFilter {
    
    // List of genres considered "premium" that require Premium or Family subscription
    private final List<String> premiumGenres = List.of("Exclusive", "New Release", "4K Ultra HD");
    
    @Override
    protected List<Content> doFilter(List<Content> contentList, User user) {
        System.out.println("[Chain of Responsibility] Applying subscription-based filter");
        
        // If user has no subscription or it's not active, only allow free content
        Subscription userSubscription = user.getSubscription();
        if (userSubscription == null || !userSubscription.isActive()) {
            return contentList.stream()
                    .filter(content -> !isPremiumContent(content))
                    .collect(Collectors.toList());
        }
        
        // If user has Basic subscription, filter out premium content
        String subscriptionType = userSubscription.getType();
        if (subscriptionType.equals(SubscriptionType.Basic.name())) {
            return contentList.stream()
                    .filter(content -> !isPremiumContent(content))
                    .collect(Collectors.toList());
        }
        
        // Premium and Family subscriptions can access all content
        return new ArrayList<>(contentList);
    }
    
    /**
     * Determines if content is premium based on its genre.
     * 
     * @param content The content to check
     * @return true if the content is premium, false otherwise
     */
    private boolean isPremiumContent(Content content) {
        return premiumGenres.contains(content.getGenre());
    }
}