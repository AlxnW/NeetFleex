package com.example.neetflex.patterns.bridge.abstraction;

import com.example.neetflex.enums.SubscriptionType;
import com.example.neetflex.patterns.bridge.impl.ISubscriptionHandler;
import com.example.neetflex.patterns.decorator.BasicSubscription;
import com.example.neetflex.patterns.decorator.ISubscription;
import com.example.neetflex.patterns.decorator.SubscriptionDecorator;
import com.example.neetflex.service.impl.SubscriptionService;
import lombok.Data;

@Data
public abstract class SubscriptionManager {

    protected ISubscriptionHandler handler;

    private ISubscription subscription = new BasicSubscription();
    SubscriptionDecorator decorator;




    public SubscriptionManager(ISubscriptionHandler handler) {
        this.handler = handler;
    }

    public abstract void subscribe(String userName, SubscriptionType type, SubscriptionService subscriptionService);


}
