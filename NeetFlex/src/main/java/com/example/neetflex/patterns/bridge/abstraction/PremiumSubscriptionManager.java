package com.example.neetflex.patterns.bridge.abstraction;

import com.example.neetflex.enums.SubscriptionType;
import com.example.neetflex.patterns.bridge.impl.ISubscriptionHandler;
import com.example.neetflex.service.impl.SubscriptionService;

public class PremiumSubscriptionManager extends SubscriptionManager {

    public PremiumSubscriptionManager(ISubscriptionHandler handler) {
        super(handler);
    }

    @Override
    public void subscribe(String userName, SubscriptionType type, SubscriptionService subscriptionService) {

        handler.activate(userName, type, subscriptionService );

    }


}
