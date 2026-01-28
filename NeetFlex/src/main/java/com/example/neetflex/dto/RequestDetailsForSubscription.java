package com.example.neetflex.dto;

import com.example.neetflex.enums.SubscriptionType;
import lombok.Data;

@Data
public class RequestDetailsForSubscription {

        private String username;
        private SubscriptionType subType;

}
