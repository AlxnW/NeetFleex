package com.example.neetflex.patterns.decorator;

public class BasicSubscription implements ISubscription {

    @Override
    public String getFeatures() {
        return "Abonament de bază: streaming la calitate standard pe 1 dispozitiv";
    }

    @Override
    public double getMonthlyCost() {
        return 7.99;
    }
}
