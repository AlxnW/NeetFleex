package com.example.neetflex.patterns.bridge.impl;

import com.example.neetflex.enums.SubscriptionType;
import com.example.neetflex.service.impl.SubscriptionService;

public interface ISubscriptionHandler {
    void activate(String userName, SubscriptionType type, SubscriptionService subscriptionService);

}
