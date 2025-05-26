package com.dev.neetfleexui.dto;

public class RequestDetailsForSubscription {
    private String username;
    private SubscriptionType subType;

    public SubscriptionType getSubType() {
        return subType;
    }

    public void setSubType(SubscriptionType subType) {
        this.subType = subType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RequestDetailsForSubscription(String username, SubscriptionType subType) {
        this.username = username;
        this.subType = subType;
    }
}
