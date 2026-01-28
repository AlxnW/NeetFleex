package com.example.neetflex.patterns.decorator;

public class MultipleDeviceDecorator extends SubscriptionDecorator{

    private final int deviceCount;

    public MultipleDeviceDecorator(ISubscription subscription, int deviceCount) {
        super(subscription);
        this.deviceCount = deviceCount;
    }

    @Override
    public String getFeatures() {
        return super.getFeatures() + ", streaming pe " + deviceCount + " dispozitive simultan";
    }

    @Override
    public double getMonthlyCost() {
        return super.getMonthlyCost() + (deviceCount * 1.5);
    }
}
