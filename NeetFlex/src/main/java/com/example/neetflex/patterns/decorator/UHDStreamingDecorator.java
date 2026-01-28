package com.example.neetflex.patterns.decorator;

public class UHDStreamingDecorator extends SubscriptionDecorator {

    public UHDStreamingDecorator(ISubscription subscription) {
        super(subscription);
    }

    @Override
    public String getFeatures() {
        return super.getFeatures() + ", streaming 4K";
    }

    @Override
    public double getMonthlyCost() {
        return super.getMonthlyCost() + 4.0; // Se adauga 4 dolari la pretul initial
    }
}
