package com.example.neetflex.patterns.decorator;

public abstract class SubscriptionDecorator implements ISubscription{
    protected ISubscription decoratedSubscription;

    public SubscriptionDecorator(ISubscription subscription) {
        this.decoratedSubscription = subscription;
    }

    @Override
    public String getFeatures() {
        return decoratedSubscription.getFeatures();
    }

    @Override
    public double getMonthlyCost() {
        return decoratedSubscription.getMonthlyCost();
    }
}
