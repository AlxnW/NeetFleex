package com.example.neetflex.patterns.FactoryMethodImpl;

import com.example.neetflex.patterns.bridge.impl.BasicSubscriptionHandler;
import com.example.neetflex.patterns.bridge.impl.ISubscriptionHandler;

public abstract class SubscriptionFactory {

    public abstract ISubscriptionHandler getSubscription() ;
}
