package com.example.neetflex.patterns.decorator;

public class OfflineViewingDecorator extends SubscriptionDecorator {
    public OfflineViewingDecorator(ISubscription subscription) {
        super(subscription);
    }

    @Override
    public String getFeatures() {
        return super.getFeatures() + ", descărcare pentru vizionare offline";
    }

    @Override
    public double getMonthlyCost() {
        return super.getMonthlyCost() + 5.0;
    }
}