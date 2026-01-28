package com.example.neetflex.patterns.bridge.impl;

import com.example.neetflex.enums.SubscriptionType;
import com.example.neetflex.patterns.FactoryMethodImpl.SubscriptionFactory;
import com.example.neetflex.patterns.decorator.BasicSubscription; // Import
import com.example.neetflex.patterns.decorator.ISubscription;    // Import
import com.example.neetflex.service.impl.SubscriptionService;
import lombok.Data;

@Data
public class BasicSubscriptionHandler extends SubscriptionFactory implements ISubscriptionHandler {

    @Override
    public void activate(String userName, SubscriptionType type, SubscriptionService subscriptionService) {
        // 1. Create the base subscription component
        ISubscription subscription = new BasicSubscription();

        // 2. Get the features and calculated price from the decorator
        String features = subscription.getFeatures();
        double calculatedPrice = subscription.getMonthlyCost();

        System.out.println("Activating Basic Subscription for user: " + userName);
        System.out.println("Features: " + features);
        System.out.println("Calculated Monthly Cost: $" + String.format("%.2f", calculatedPrice));

        // 3. Use the calculated price when subscribing the user
        // Assuming SubscriptionType.BASIC is appropriate here, or use the 'type' parameter if it's guaranteed to be BASIC
        subscriptionService.subscribeUser(userName, SubscriptionType.Basic, calculatedPrice);

        System.out.println("Basic subscription activated for user " + userName + " with a monthly cost of $" + String.format("%.2f", calculatedPrice));
    }

    @Override
    public ISubscriptionHandler getSubscription() {
        return new BasicSubscriptionHandler();
    }
}