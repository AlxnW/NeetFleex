package com.example.neetflex.patterns.bridge.impl;

import com.example.neetflex.enums.SubscriptionType;
import com.example.neetflex.patterns.FactoryMethodImpl.SubscriptionFactory;
import com.example.neetflex.patterns.decorator.BasicSubscription;     // Import
import com.example.neetflex.patterns.decorator.ISubscription;        // Import
import com.example.neetflex.patterns.decorator.OfflineViewingDecorator; // Import
// Import UHDStreamingDecorator if Family plan also includes it
// import com.example.neetflex.patterns.decorator.UHDStreamingDecorator;
import com.example.neetflex.service.impl.SubscriptionService;

public class FamilySubscriptionHandler extends SubscriptionFactory implements ISubscriptionHandler {

    @Override
    public void activate(String userName, SubscriptionType type, SubscriptionService subscriptionService) {
        // 1. Create the base subscription component
        ISubscription subscription = new BasicSubscription();

        // 2. Wrap it with decorators for Family features
        // For example, let's assume Family plan includes Offline Viewing
        subscription = new OfflineViewingDecorator(subscription);
        // If Family also includes UHD, you would add:
        // subscription = new UHDStreamingDecorator(subscription);

        // 3. Get the features and calculated price from the decorated object
        String features = subscription.getFeatures();
        double calculatedPrice = subscription.getMonthlyCost();

        System.out.println("Activating Family Subscription for user: " + userName);
        System.out.println("Features: " + features);
        System.out.println("Calculated Monthly Cost: $" + String.format("%.2f", calculatedPrice));

        // 4. Use the calculated price when subscribing the user
        // Assuming SubscriptionType.FAMILY is appropriate, or use the 'type' parameter
        subscriptionService.subscribeUser(userName, SubscriptionType.Family, calculatedPrice);

        System.out.println("Family subscription activated for user " + userName + " with a monthly cost of $" + String.format("%.2f", calculatedPrice));
    }

    @Override
    public ISubscriptionHandler getSubscription() {
        return new FamilySubscriptionHandler();
    }
}